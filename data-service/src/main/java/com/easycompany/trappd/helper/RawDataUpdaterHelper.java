package com.easycompany.trappd.helper;

import com.easycompany.trappd.cache.CityCache;
import com.easycompany.trappd.cache.StateCache;
import com.easycompany.trappd.exception.UpdateException;
import com.easycompany.trappd.mapper.CovidCaseDtoMapper;
import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.dto.CaseDtoV2;
import com.easycompany.trappd.model.dto.CovidCaseExtraInfoDto;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.StateEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CountryRepository;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.repository.StateRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class RawDataUpdaterHelper extends DataUpdaterHelper<CaseDtoV2, CovidCaseEntity> {

  private final CovidCaseRepository covidCaseRepository;

  private final CovidCaseDtoMapper covidCaseDtoMapper;
  private final CityRepository cityRepository;

  private final StateRepository stateRepository;

  private final CountryRepository countryRepository;

  private final ObjectMapper objectMapper;

  private final CityCache cityCache;
  private final StateCache stateCache;

  @Autowired
  public RawDataUpdaterHelper(
      CovidCaseRepository covidCaseRepository,
      CovidCaseDtoMapper covidCaseDtoMapper,
      CityRepository cityRepository,
      StateRepository stateRepository,
      CountryRepository countryRepository,
      ObjectMapper objectMapper,
      CityCache cityCache,
      StateCache stateCache) {
    this.covidCaseRepository = covidCaseRepository;
    this.covidCaseDtoMapper = covidCaseDtoMapper;
    this.cityRepository = cityRepository;
    this.stateRepository = stateRepository;
    this.countryRepository = countryRepository;
    this.objectMapper = objectMapper;
    this.cityCache = cityCache;
    this.stateCache = stateCache;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void update(List<CaseDtoV2> latestCaseList) {
    log.info("Starting update process for raw data cases");
    if (latestCaseList == null) {
      onUpdateError(
          new UpdateException(
              "Returned raw data cases is null from S3/local file. Skipping update"));
      return;
    }
    // Retrieve all active cases from DB
    log.debug("Retrieving all cases from table [covid_case]");
    List<CovidCaseEntity> listOfExistingRecords = covidCaseRepository.findAll();
    if (listOfExistingRecords.size() == 0) {
      // Bulk insert all records
      log.debug("Bulk inserting all records as there is no history present");
      bulkInsert(latestCaseList);
    } else {
      // One to one check with hisotry and update records
      log.debug("Checking latest data against old records to update");
      checkAndUpdate(latestCaseList, listOfExistingRecords);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void checkAndUpdate(
      List<CaseDtoV2> latestCaseList, List<CovidCaseEntity> listOfExistingRecords) {
    Map<String, CovidCaseEntity> patientIdCovidCaseEntityMap =
        listOfExistingRecords.stream()
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
                if (shouldUpdate(caseDto, covidCaseEntity)) {
                  oldCasesForBulkUpdate.add(covidCaseEntity);
                }
              }
            });

    if (newCasesForBulkInsert.size() > 0) {
      // Bulk update new cases
      log.debug("Bulk inserting new records");
      bulkInsert(newCasesForBulkInsert);
    } else {
      log.debug("There are no records to insert");
    }
    if (oldCasesForBulkUpdate.size() > 0) {
      // Bulk update new cases
      log.debug("Bulk updating new records");
      bulkUpdate(oldCasesForBulkUpdate);
    } else {
      log.debug("There are no records to update");
    }
    onUpdateSuccess("Raw data update process finished successfully");
  }

  @Override
  public void bulkUpdate(List<CovidCaseEntity> recordsToUpdate) {
    // covidCaseRepository.saveAll(covidCaseEntitiesToUpdate);
    log.debug("bulkUpdate() called");
    log.debug("Bulk updated {} records successfully", recordsToUpdate.size());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void bulkInsert(List<CaseDtoV2> recordsToInsert) {
    log.debug("bulkInsert() called");
    List<CovidCaseEntity> covidCaseEntities =
        recordsToInsert.stream()
            .filter(caseDto -> !StringUtils.isEmpty(caseDto.getStateCode()))
            .map(
                caseDto -> {
                  CovidCaseEntity covidCaseEntity = covidCaseDtoMapper.toCovidCaseEntity(caseDto);
                  // Check if City code is null
                  CityEntity cityEntity = null;
                  /*if (caseDto.getDetectedCity() != null && caseDto.getDetectedDistrict() != null) {
                    List<CityEntity> cityEntities =
                        cityRepository.findAllByCodeIgnoreCaseOrCodeIgnoreCase(
                            caseDto.getDetectedCity(), caseDto.getDetectedDistrict());
                    cityEntity = cityEntities.stream().findFirst().orElse(null);
                  }*/
                  cityEntity =
                      cityCache.getCityEntity(
                          caseDto.getDetectedCity() + caseDto.getStateCode(),
                          caseDto.getDetectedDistrict() + caseDto.getStateCode());
                  covidCaseEntity.setCity(cityEntity);
                  StateEntity stateEntity =
                      cityEntity != null
                          ? cityEntity.getState()
                          : stateCache.getStateEntity(caseDto.getStateCode()).orElse(null);
                  if (stateEntity == null) {
                    return null;
                  }
                  CountryEntity countryEntity = stateEntity.getCountry();

                  if (cityEntity == null) {
                    // log.trace("City is null for [{}]", caseDto.getPatientNumber());
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

  @Override
  boolean shouldUpdate(CaseDtoV2 dto, CovidCaseEntity entity) {
    if (entity.getAnnouncedDate().compareTo(covidCaseDtoMapper.parseDate(dto.getDateAnnounced()))
        != 0) {
      log.trace(
          "Announced date changed for pid {} from {} to {}",
          entity.getPatientNumber(),
          DateTimeUtil.formatLocalDate(
              entity.getAnnouncedDate(), AppConstants.ANNOUNCED_DATE_FORMAT),
          dto.getDateAnnounced());
      entity.setAnnouncedDate(covidCaseDtoMapper.parseDate(dto.getDateAnnounced()));
      return true;
    }
    return false;
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
}
