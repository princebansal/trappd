package com.easycompany.trappd.service;

import com.easycompany.trappd.config.AwsClient;
import com.easycompany.trappd.exception.DataUpdaterServiceException;
import com.easycompany.trappd.mapper.CovidCaseDtoMapper;
import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.dto.CaseDtoV2;
import com.easycompany.trappd.model.dto.CovidCaseExtraInfoDto;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.DataUploadStatusHistoryEntity;
import com.easycompany.trappd.model.entity.StateEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CountryRepository;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.repository.DataUploadStatusHistoryRepository;
import com.easycompany.trappd.repository.StateRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@Transactional
@Deprecated
public class DataUpdaterService {

  private final DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository;

  private final CovidCaseRepository covidCaseRepository;

  private final CovidCaseDtoMapper covidCaseDtoMapper;

  private final AwsClient awsClient;

  private final ObjectMapper objectMapper;

  private final CityRepository cityRepository;

  private final StateRepository stateRepository;

  private final CountryRepository countryRepository;

  @Autowired
  public DataUpdaterService(
      DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository,
      CovidCaseRepository covidCaseRepository,
      AwsClient awsClient,
      ObjectMapper objectMapper,
      CovidCaseDtoMapper covidCaseDtoMapper,
      CityRepository cityRepository,
      StateRepository stateRepository,
      CountryRepository countryRepository) {
    this.dataUploadStatusHistoryRepository = dataUploadStatusHistoryRepository;
    this.covidCaseRepository = covidCaseRepository;
    this.awsClient = awsClient;
    this.objectMapper = objectMapper;
    this.covidCaseDtoMapper = covidCaseDtoMapper;
    this.cityRepository = cityRepository;
    this.stateRepository = stateRepository;
    this.countryRepository = countryRepository;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void update() throws DataUpdaterServiceException, IOException {
    DataUploadStatusHistoryEntity lastDataUploadStatusHistoryEntity =
        dataUploadStatusHistoryRepository
            .findFirstByOrderByUploadDateDesc()
            .orElseThrow(() -> new DataUpdaterServiceException("No records found in the database"));
    if (lastDataUploadStatusHistoryEntity.getProcessingStatus() != ProcessingStatus.PENDING) {
      log.info(
          "Skipping update. Last row is in the state {}.",
          lastDataUploadStatusHistoryEntity.getProcessingStatus().name());
      return;
    }
    startProcessingUpdate(lastDataUploadStatusHistoryEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected void startProcessingUpdate(
      DataUploadStatusHistoryEntity lastDataUploadStatusHistoryEntity) throws IOException {
    log.debug("Starting update process");

    // Mark record as processing
    log.debug("Marking record status @ProcessingStatus.PROCESSING");
    lastDataUploadStatusHistoryEntity.setProcessingStatus(ProcessingStatus.PROCESSING);
    dataUploadStatusHistoryRepository.save(lastDataUploadStatusHistoryEntity);

    // Retrieve snapshot from S3 and parsing
    log.debug("Retrieving snapshot from S3 and deserializing");
    List<CaseDtoV2> latestCaseList = getCaseListFromHistory(lastDataUploadStatusHistoryEntity);

    if (latestCaseList == null) {
      log.error("Returned cases is null from S3/local file. Skipping update");
      return;
    }
    // Retrieve all active cases from DB
    log.debug("Retrieving all @CaseStatus.ACTIVE cases from table [covid_case]");
    List<CovidCaseEntity> latestExistingRecords = covidCaseRepository.findAll();

    if (latestExistingRecords.size() == 0) {
      // Bulk insert all records
      log.debug("Bulk inserting all records as there is no history present");
      bulkInsertAllRecords(latestCaseList);
    } else {
      // One to one check with hisotry and update records
      log.debug("Checking latest data against old records to update");
      checkAndUpdateRecordsWitHistory(latestCaseList, latestExistingRecords);
    }

    log.debug("Marking record status @ProcessingStatus.PROCESSED");
    lastDataUploadStatusHistoryEntity.setProcessingStatus(ProcessingStatus.PROCESSED);
    dataUploadStatusHistoryRepository.save(lastDataUploadStatusHistoryEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected void checkAndUpdateRecordsWitHistory(
      List<CaseDtoV2> latestCaseList, List<CovidCaseEntity> latestExistingRecords) {

    Map<String, CovidCaseEntity> patientIdCovidCaseEntityMap =
        latestExistingRecords.stream()
            .collect(
                Collectors.toMap(
                    covidCaseEntity -> covidCaseEntity.getPatientNumber(),
                    covidCaseEntity -> covidCaseEntity));

    List<CaseDtoV2> newCasesForBulkInsert = new ArrayList<>();
    List<CovidCaseEntity> oldCasesForBulkUpdate = new ArrayList<>();
    latestCaseList.stream()
        .forEach(
            caseDto -> {
              if (!patientIdCovidCaseEntityMap.containsKey(caseDto.getPatientNumber())) {
                newCasesForBulkInsert.add(caseDto);
              } else {
                CovidCaseEntity covidCaseEntity =
                    patientIdCovidCaseEntityMap.get(caseDto.getPatientNumber());
                boolean shouldUpdateRecord;

                shouldUpdateRecord =
                    checkDateAnnounced(caseDto, covidCaseEntity)
                        || checkCaseStatus(caseDto, covidCaseEntity);
                ;
                if (shouldUpdateRecord) {
                  oldCasesForBulkUpdate.add(covidCaseEntity);
                }
              }
            });

    if (newCasesForBulkInsert.size() > 0) {
      // Bulk update new cases
      log.debug("Bulk inserting new records");
      bulkInsertAllRecords(newCasesForBulkInsert);
    } else {
      log.debug("There are no records to insert");
    }
    if (oldCasesForBulkUpdate.size() > 0) {
      // Bulk update new cases
      log.debug("Bulk updating new records");
      bulkUpdateAllRecords(oldCasesForBulkUpdate);
    } else {
      log.debug("There are no records to update");
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected boolean checkCaseStatus(CaseDtoV2 caseDtoV2, CovidCaseEntity covidCaseEntity) {

    if (covidCaseEntity.getStatus()
        != CovidCaseDtoMapper.stringToCaseStatusEnum(caseDtoV2.getCurrentStatus())) {

      log.trace(
          "Case status changed for pid {} from {} to {}",
          covidCaseEntity.getPatientNumber(),
          covidCaseEntity.getStatus(),
          CovidCaseDtoMapper.stringToCaseStatusEnum(caseDtoV2.getCurrentStatus()));
      covidCaseEntity.setStatus(
          CovidCaseDtoMapper.stringToCaseStatusEnum(caseDtoV2.getCurrentStatus()));
      setStatusDateForCovidCaseEnitity(caseDtoV2, covidCaseEntity);
      return true;
    }
    return false;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected boolean checkDateAnnounced(CaseDtoV2 caseDtoV2, CovidCaseEntity covidCaseEntity) {
    if (covidCaseEntity
            .getAnnouncedDate()
            .compareTo(covidCaseDtoMapper.parseDate(caseDtoV2.getDateAnnounced()))
        != 0) {
      log.trace(
          "Announced date changed for pid {} from {} to {}",
          covidCaseEntity.getPatientNumber(),
          DateTimeUtil.formatLocalDate(
              covidCaseEntity.getAnnouncedDate(), AppConstants.ANNOUNCED_DATE_FORMAT),
          caseDtoV2.getDateAnnounced());
      covidCaseEntity.setAnnouncedDate(covidCaseDtoMapper.parseDate(caseDtoV2.getDateAnnounced()));
      return true;
    }
    return false;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected void bulkUpdateAllRecords(List<CovidCaseEntity> covidCaseEntitiesToUpdate) {
    // covidCaseRepository.saveAll(covidCaseEntitiesToUpdate);
    log.debug("bulkUpdateAllRecords() called");
    log.debug("Bulk updated {} records successfully", covidCaseEntitiesToUpdate.size());
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected void bulkInsertAllRecords(List<CaseDtoV2> latestExistingRecords) {
    log.debug("bulkInsertAllRecords() called");
    List<CovidCaseEntity> covidCaseEntities =
        latestExistingRecords.stream()
            .filter(caseDto -> !StringUtils.isEmpty(caseDto.getStateCode()))
            .map(
                caseDto -> {
                  CovidCaseEntity covidCaseEntity = covidCaseDtoMapper.toCovidCaseEntity(caseDto);
                  // Check if City code is null
                  CityEntity cityEntity = null;
                  if (caseDto.getDetectedCity() != null && caseDto.getDetectedDistrict() != null) {
                    List<CityEntity> cityEntities =
                        cityRepository.findAllByCodeIgnoreCaseOrCodeIgnoreCase(
                            caseDto.getDetectedCity(), caseDto.getDetectedDistrict());
                    cityEntity = cityEntities.stream().findFirst().orElse(null);
                  }
                  covidCaseEntity.setCity(cityEntity);
                  StateEntity stateEntity =
                      cityEntity != null
                          ? cityEntity.getState()
                          : stateRepository
                              .findByCodeIgnoreCase(caseDto.getStateCode())
                              .orElse(null);
                  if (stateEntity == null) {
                    return null;
                  }
                  CountryEntity countryEntity = stateEntity.getCountry();

                  if (cityEntity == null) {
                    log.trace("City is null for [{}]", caseDto.getPatientNumber());
                  }
                  if (stateEntity == null) {
                    log.trace("State is null for [{}]", caseDto.getPatientNumber());
                  }
                  if (countryEntity == null) {
                    log.trace("Country is null for [{}]", caseDto.getPatientNumber());
                  }
                  covidCaseEntity.setState(stateEntity);
                  covidCaseEntity.setCountry(countryEntity);
                  setStatusDateForCovidCaseEnitity(caseDto, covidCaseEntity);
                  covidCaseEntity.setExtraInfo(getExtraInfo(caseDto));
                  return covidCaseEntity;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    covidCaseRepository.saveAll(covidCaseEntities);
    log.debug("Bulk inserted {} records successfully", covidCaseEntities.size());
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected void setStatusDateForCovidCaseEnitity(
      CaseDtoV2 caseDtoV2, CovidCaseEntity covidCaseEntity) {
    if (caseDtoV2.getStatusChangeDate() != null) {
      if (covidCaseEntity.getStatus() == CaseStatus.RECOVERED) {
        covidCaseEntity.setRecoveredDate(
            DateTimeUtil.parseLocalDate(
                caseDtoV2.getStatusChangeDate(), AppConstants.STATUS_CHANGE_DATE_FORMAT));
      } else if (covidCaseEntity.getStatus() == CaseStatus.DECEASED) {
        covidCaseEntity.setDeceasedDate(
            DateTimeUtil.parseLocalDate(
                caseDtoV2.getStatusChangeDate(), AppConstants.STATUS_CHANGE_DATE_FORMAT));
      }
    }
  }

  private byte[] getExtraInfo(CaseDtoV2 caseDtoV2) {
    try {
      return objectMapper
          .writeValueAsString(
              CovidCaseExtraInfoDto.builder()
                  .statePatientNumber(caseDtoV2.getStatePatientNumber())
                  .backupNotes(caseDtoV2.getBackupNotes())
                  .contactPatient(caseDtoV2.getContactPatient())
                  .nationality(caseDtoV2.getNationality())
                  .notes(caseDtoV2.getNotes())
                  .source1(caseDtoV2.getSource1())
                  .source2(caseDtoV2.getSource2())
                  .source3(caseDtoV2.getSource3())
                  .build())
          .getBytes();
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected List<CaseDtoV2> getCaseListFromHistory(
      DataUploadStatusHistoryEntity lastDataUploadStatusHistoryEntity) throws IOException {
    InputStream inputStream =
        awsClient.downloadFile(lastDataUploadStatusHistoryEntity.getFilePath());
    if (inputStream != null) {
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

        String data = bufferedReader.lines().collect(Collectors.joining());
        log.info("Read is successful.");
        log.debug("Partial dump [{}]", data.length() > 1000 ? data.substring(0, 1000) : data);

        return objectMapper.readValue(
            data, objectMapper.getTypeFactory().constructCollectionType(List.class, CaseDtoV2.class));
      } catch (Exception e) {
        log.error("Error occurred in file download/processing {}", e);
        throw e;
      }
    } else {
      log.error("Returned stream is null");
      return null;
    }
  }
}
