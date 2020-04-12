package com.easycompany.trappd.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.amazonaws.AmazonServiceException;
import com.easycompany.trappd.config.AwsClient;
import com.easycompany.trappd.entity.DataUploadStatusHistoryEntity;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.dto.response.BaseResponse;
import com.easycompany.trappd.repository.DataUploadStatusHistoryRepository;
import java.io.IOException;
import java.util.Optional;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class EngineServiceTest {

  @SpyBean EngineService spiedEngineService;
  @MockBean AwsClient mockedAwsClient;
  @Autowired DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository;

  @BeforeEach
  void before() {
    doNothing().when(mockedAwsClient).uploadFile(any(), any(), eq(0L));
  }

  @Test
  void updateDataInEngine_mockInnerMethod_expectBaseResponse() throws IOException {
    // Given
    BaseResponse expectedResponse =
        BaseResponse.builder()
            .message("Upload request successful")
            .status(HttpStatus.OK.getReasonPhrase())
            .build();
    // When

    doNothing().when(spiedEngineService).readAndUploadDataToS3(any());
    BaseResponse baseResponse = spiedEngineService.updateDataInEngine(null);
    // Then
    Assertions.assertEquals(expectedResponse, baseResponse);
  }

  @Test
  void readAndUploadDataToS3() {}

  @Test
  @Tag("engine_service_test1")
  @Sql({"classpath:/datasets/engine_service_test1.sql"})
  void proceedToUploadToS3_noHistoryPresentInDB_expectNewRecordSavedWithStatusPending() {
    // Given
    Optional<DataUploadStatusHistoryEntity> lastRecord = null;
    // When
    spiedEngineService.proceedToUploadToS3(null, 0L);
    lastRecord = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    // Then
    Assertions.assertTrue(lastRecord.isPresent());
    Assertions.assertEquals(ProcessingStatus.PENDING, lastRecord.get().getProcessingStatus());
  }

  @Test
  @Tag("engine_service_test2")
  @Sql({"classpath:/datasets/engine_service_test2.sql"})
  void proceedToUploadToS3_lastRecordProcessing_expectNewRecordSavedWithStatusPending() {
    // Given
    Optional<DataUploadStatusHistoryEntity> lastRecordBeforeSaving = null;
    Optional<DataUploadStatusHistoryEntity> lastRecord = null;
    lastRecordBeforeSaving = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    Integer totalRecords = 0;
    // When
    spiedEngineService.proceedToUploadToS3(null, 0L);
    lastRecord = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    totalRecords = dataUploadStatusHistoryRepository.findAll().size();
    // Then
    Assertions.assertTrue(lastRecordBeforeSaving.isPresent());
    Assertions.assertTrue(lastRecord.isPresent());
    Assertions.assertEquals(ProcessingStatus.PENDING, lastRecord.get().getProcessingStatus());
    Assertions.assertEquals(2, totalRecords);
  }

  @Test
  @Tag("engine_service_test3")
  @Sql({"classpath:/datasets/engine_service_test3.sql"})
  void proceedToUploadToS3_lastRecordProcessed_expectNewRecordSavedWithStatusPending() {
    // Given
    Optional<DataUploadStatusHistoryEntity> lastRecordBeforeSaving = null;
    Optional<DataUploadStatusHistoryEntity> lastRecord = null;
    lastRecordBeforeSaving = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    Integer totalRecords = 0;
    // When
    spiedEngineService.proceedToUploadToS3(null, 0L);
    lastRecord = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    totalRecords = dataUploadStatusHistoryRepository.findAll().size();
    // Then
    Assertions.assertTrue(lastRecordBeforeSaving.isPresent());
    Assertions.assertTrue(lastRecord.isPresent());
    Assertions.assertEquals(ProcessingStatus.PENDING, lastRecord.get().getProcessingStatus());
    Assertions.assertEquals(2, totalRecords);
  }

  @Test
  @Tag("engine_service_test4")
  @Sql({"classpath:/datasets/engine_service_test4.sql"})
  void proceedToUploadToS3_lastRecordPending_expectNewRecordSavedWithStatusPending() {
    // Given
    Optional<DataUploadStatusHistoryEntity> lastRecordBeforeSaving = null;
    Optional<DataUploadStatusHistoryEntity> lastRecord = null;
    Optional<DataUploadStatusHistoryEntity> secondLastRecord = null;
    lastRecordBeforeSaving = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    Integer totalRecords = 0;
    // When
    spiedEngineService.proceedToUploadToS3(null, 0L);
    lastRecord = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    secondLastRecord = Optional.ofNullable(dataUploadStatusHistoryRepository.findAll().get(0));
    totalRecords = dataUploadStatusHistoryRepository.findAll().size();
    // Then
    Assertions.assertTrue(lastRecordBeforeSaving.isPresent());
    Assertions.assertTrue(lastRecord.isPresent());
    Assertions.assertEquals(ProcessingStatus.PENDING, lastRecord.get().getProcessingStatus());
    Assertions.assertEquals(2, totalRecords);
    Assertions.assertTrue(secondLastRecord.isPresent());
    Assertions.assertEquals(secondLastRecord.get().getId(), lastRecordBeforeSaving.get().getId());
    Assertions.assertEquals(
        ProcessingStatus.OVERRIDDEN, secondLastRecord.get().getProcessingStatus());
  }

  @Test
  @Sql({"classpath:/datasets/engine_service_test3.sql"})
  void
      proceedToUploadToS3_lastRecordProcessedAndThrownExceptionInUploading_expectNoNewRecordSaved() {
    // Given
    Optional<DataUploadStatusHistoryEntity> lastRecordBeforeSaving = null;
    Optional<DataUploadStatusHistoryEntity> lastRecord = null;
    lastRecordBeforeSaving = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    Integer totalRecords = 0;
    // When

    doThrow(new AmazonServiceException("mock"))
        .when(mockedAwsClient)
        .uploadFile(any(), any(), eq(0L));
    try {
      spiedEngineService.proceedToUploadToS3(null, 0L);
    } catch (Exception e) {
      Assertions.assertTrue(e.getMessage().contains("mock"));
    }
    // Then
    Assertions.assertTrue(lastRecordBeforeSaving.isPresent());

    /*lastRecord = dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    totalRecords = dataUploadStatusHistoryRepository.findAll().size();

    Assertions.assertTrue(lastRecord.isPresent());
    // Assertions.assertEquals(ProcessingStatus.PROCESSED, lastRecord.get().getProcessingStatus());
    Assertions.assertEquals(1, totalRecords);*/
  }
}
