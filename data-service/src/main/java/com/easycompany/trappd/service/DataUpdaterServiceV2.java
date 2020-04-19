package com.easycompany.trappd.service;

import com.easycompany.trappd.config.AwsClient;
import com.easycompany.trappd.exception.DataUpdaterServiceException;
import com.easycompany.trappd.helper.DeathAndRecoveryUpdaterHelper;
import com.easycompany.trappd.helper.RawDataUpdaterHelper;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.dto.CaseDtoV2;
import com.easycompany.trappd.model.dto.DeathAndRecoveryDto;
import com.easycompany.trappd.model.dto.DumpedDataDto;
import com.easycompany.trappd.model.entity.DataUploadStatusHistoryEntity;
import com.easycompany.trappd.repository.DataUploadStatusHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class DataUpdaterServiceV2 {

  private final DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository;

  private final AwsClient awsClient;

  private final ObjectMapper objectMapper;

  private final RawDataUpdaterHelper rawDataUpdaterHelper;
  private final DeathAndRecoveryUpdaterHelper deathAndRecoveryUpdaterHelper;

  @Autowired
  public DataUpdaterServiceV2(
      DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository,
      AwsClient awsClient,
      ObjectMapper objectMapper,
      RawDataUpdaterHelper rawDataUpdaterHelper,
      DeathAndRecoveryUpdaterHelper deathAndRecoveryUpdaterHelper) {
    this.dataUploadStatusHistoryRepository = dataUploadStatusHistoryRepository;
    this.awsClient = awsClient;
    this.objectMapper = objectMapper;
    this.rawDataUpdaterHelper = rawDataUpdaterHelper;
    this.deathAndRecoveryUpdaterHelper = deathAndRecoveryUpdaterHelper;
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
    DumpedDataDto dumpedDataDto = getCaseListFromHistory(lastDataUploadStatusHistoryEntity);
    List<CaseDtoV2> latestCaseList = dumpedDataDto.getCaseDtoV2List();
    List<DeathAndRecoveryDto> latestDeathAndRecoveryList =
        dumpedDataDto.getDeathAndRecoveryDtoList();
    rawDataUpdaterHelper.update(latestCaseList);
    deathAndRecoveryUpdaterHelper.update(latestDeathAndRecoveryList);

    log.debug("Marking record status @ProcessingStatus.PROCESSED");
    lastDataUploadStatusHistoryEntity.setProcessingStatus(ProcessingStatus.PROCESSED);
    dataUploadStatusHistoryRepository.save(lastDataUploadStatusHistoryEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected DumpedDataDto getCaseListFromHistory(
      DataUploadStatusHistoryEntity lastDataUploadStatusHistoryEntity) throws IOException {
    InputStream inputStream =
        awsClient.downloadFile(lastDataUploadStatusHistoryEntity.getFilePath());
    if (inputStream != null) {
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

        String data = bufferedReader.lines().collect(Collectors.joining());
        log.info("Read is successful.");
        log.debug("Partial dump [{}]", data.length() > 1000 ? data.substring(0, 1000) : data);

        return objectMapper.readValue(data, DumpedDataDto.class);
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
