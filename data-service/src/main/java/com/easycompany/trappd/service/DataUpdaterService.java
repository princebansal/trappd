package com.easycompany.trappd.service;

import com.easycompany.trappd.config.AwsClient;
import com.easycompany.trappd.exception.DataUpdaterServiceException;
import com.easycompany.trappd.mapper.CovidCaseDtoMapper;
import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.dto.CaseDto;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.DataUploadStatusHistoryEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CovidCaseEntityRepository;
import com.easycompany.trappd.repository.DataUploadStatusHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DataUpdaterService {

  private final DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository;

  private final CovidCaseEntityRepository covidCaseEntityRepository;

  private final CovidCaseDtoMapper covidCaseDtoMapper;

  private final AwsClient awsClient;

  private final ObjectMapper objectMapper;

  private final CityRepository cityRepository;

  @Autowired
  public DataUpdaterService(
      DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository,
      CovidCaseEntityRepository covidCaseEntityRepository,
      AwsClient awsClient,
      ObjectMapper objectMapper,
      CovidCaseDtoMapper covidCaseDtoMapper,
      CityRepository cityRepository) {
    this.dataUploadStatusHistoryRepository = dataUploadStatusHistoryRepository;
    this.covidCaseEntityRepository = covidCaseEntityRepository;
    this.awsClient = awsClient;
    this.objectMapper = objectMapper;
    this.covidCaseDtoMapper = covidCaseDtoMapper;
    this.cityRepository = cityRepository;
  }

  @SneakyThrows
  public void update() {
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
    List<CaseDto> latestCaseList = getCaseListFromHistory(lastDataUploadStatusHistoryEntity);

    // Retrieve all active cases from DB
    log.debug("Retrieving all @CaseStatus.ACTIVE cases from table [covid_case]");
    List<CovidCaseEntity> latestExistingRecords =
        covidCaseEntityRepository.findAllByStatus(CaseStatus.ACTIVE);

    if (latestExistingRecords.size() == 0) {
      bulkInsertAllRecords(latestCaseList);
    } else {
      checkAndUpdateRecordsWitHistory(latestCaseList, latestExistingRecords);
    }
  }

  private void checkAndUpdateRecordsWitHistory(
      List<CaseDto> latestCaseList, List<CovidCaseEntity> la) {}

  private void bulkInsertAllRecords(List<CaseDto> latestExistingRecords) {
    List<CovidCaseEntity> covidCaseEntities =
        latestExistingRecords.stream()
            .map(
                caseDto -> {
                  CovidCaseEntity covidCaseEntity = covidCaseDtoMapper.toCovidCaseEntity(caseDto);
                  CityEntity cityEntity =
                      cityRepository.findByCode(caseDto.getDetectedCity()).get();
                  covidCaseEntity.setCity(cityEntity);
                  return covidCaseEntity;
                })
            .collect(Collectors.toList());
    covidCaseEntityRepository.saveAll(covidCaseEntities);
  }

  private List<CaseDto> getCaseListFromHistory(
      DataUploadStatusHistoryEntity lastDataUploadStatusHistoryEntity) throws IOException {
    InputStream inputStream =
        awsClient.downloadFile(lastDataUploadStatusHistoryEntity.getFilePath());
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

      String data = bufferedReader.lines().collect(Collectors.joining());
      log.info("Read is successful.");
      log.debug("Partial dump [{}]", data.length() > 1000 ? data.substring(0, 1000) : data);

      return objectMapper.readValue(
          data, objectMapper.getTypeFactory().constructCollectionType(List.class, CaseDto.class));
    } catch (Exception e) {
      log.error("Error occurred in file download/processing {}", e);
      throw e;
    }
  }
}
