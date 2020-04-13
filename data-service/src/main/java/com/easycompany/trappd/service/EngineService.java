package com.easycompany.trappd.service;

import com.easycompany.trappd.config.AwsClient;
import com.easycompany.trappd.model.entity.DataUploadStatusHistoryEntity;
import com.easycompany.trappd.exception.NoDataFoundToUploadException;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.dto.response.BaseResponse;
import com.easycompany.trappd.repository.DataUploadStatusHistoryRepository;
import com.easycompany.trappd.util.DateTimeUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class EngineService {

  final AwsClient awsClient;

  final DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository;

  @Autowired
  public EngineService(
      AwsClient awsClient, DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository) {
    this.awsClient = awsClient;
    this.dataUploadStatusHistoryRepository = dataUploadStatusHistoryRepository;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public BaseResponse updateDataInEngine(MultipartFile multipartFile) throws IOException {
    readAndUploadDataToS3(multipartFile);
    return BaseResponse.builder()
        .message("Upload request successful")
        .status(HttpStatus.OK.getReasonPhrase())
        .build();
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRED)
  protected void readAndUploadDataToS3(MultipartFile multipartFile) {

    log.info("Starting to read the request payload");
    try (BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {

      bufferedReader
          .lines()
          .findFirst()
          .orElseThrow(
              () -> new NoDataFoundToUploadException("Data sent from Trappd engine is null/blank"));
      log.info("Partial read is successful. Moving forward to upload");
      proceedToUploadToS3(
          multipartFile.getInputStream(), multipartFile.getResource().contentLength());
    } catch (IOException | NoDataFoundToUploadException e) {
      log.error("Error occurred in data processing {}", e);
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected void proceedToUploadToS3(InputStream inputStream, long contentLength) {
    DataUploadStatusHistoryEntity dataUploadStatusHistoryEntity =
        dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc().orElse(null);

    String fileName = UUID.randomUUID().toString();
    fileName = fileName + ".dat";
    DataUploadStatusHistoryEntity newDataUploadStatusHistoryEntity =
        DataUploadStatusHistoryEntity.builder()
            .filePath(fileName)
            .processingStatus(ProcessingStatus.PENDING)
            .uploadDate(DateTimeUtil.currentTimeInUTC())
            .build();
    if (dataUploadStatusHistoryEntity != null
        && dataUploadStatusHistoryEntity.getProcessingStatus() == ProcessingStatus.PENDING) {
      // Update the status to OVERRIDDEN
      dataUploadStatusHistoryEntity.setProcessingStatus(ProcessingStatus.OVERRIDDEN);
      dataUploadStatusHistoryRepository.save(dataUploadStatusHistoryEntity);
    }

    dataUploadStatusHistoryRepository.save(newDataUploadStatusHistoryEntity);

    awsClient.uploadFile(inputStream, fileName, contentLength);
  }
}
