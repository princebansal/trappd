package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.entity.DataUploadStatusHistoryEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class DataUploadStatusHistoryRepositoryTest {

  @Autowired private DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository;

  @Test
  @Sql({"classpath:/datasets/data_upload_status_history.sql"})
  public void findFirstByOrderByUploadDateDesc_supplySampleDataSet_expectLastRow() {
    // Given
    Optional<DataUploadStatusHistoryEntity> dataUploadStatusHistoryEntity = null;
    // When
    dataUploadStatusHistoryEntity =
        dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    // Then
    Assertions.assertTrue(dataUploadStatusHistoryEntity.isPresent());
    Assertions.assertEquals("e", dataUploadStatusHistoryEntity.get().getFilePath());
  }

  @Test
  @Sql({"classpath:/datasets/data_upload_status_history.sql"})
  public void findByOrderByUploadDateDesc_definePageableLimit_expectNumberOfRowsEqualToLimit() {
    // Given
    List<DataUploadStatusHistoryEntity> dataUploadStatusHistoryEntity = null;
    // When
    dataUploadStatusHistoryEntity =
        dataUploadStatusHistoryRepository.findByOrderByUploadDateDesc(PageRequest.of(0, 2));
    // Then
    Assertions.assertNotNull(dataUploadStatusHistoryEntity);
    Assertions.assertEquals(2, dataUploadStatusHistoryEntity.size());
    Assertions.assertEquals("e", dataUploadStatusHistoryEntity.get(0).getFilePath());
    Assertions.assertEquals("d", dataUploadStatusHistoryEntity.get(1).getFilePath());
  }

  @Test
  @Tag("data_upload_status_history_test1")
  @Sql({"classpath:/datasets/data_upload_status_history_test1.sql"})
  public void
      findFirstByAndProcessingStatusAndOrderByUploadDateDesc_defineSupplyStatus_expectSingleLastResult() {
    // Given
    Optional<DataUploadStatusHistoryEntity> dataUploadStatusHistoryEntity = null;
    // When
    dataUploadStatusHistoryEntity =
        dataUploadStatusHistoryRepository.findFirstByAndProcessingStatusEqualsOrderByUploadDateDesc(
            ProcessingStatus.PENDING);
    // Then
    Assertions.assertTrue(dataUploadStatusHistoryEntity.isPresent());
    Assertions.assertEquals("e", dataUploadStatusHistoryEntity.get().getFilePath());
  }

  @Test
  @Tag("data_upload_status_history_test1")
  @Sql({"classpath:/datasets/data_upload_status_history_test1.sql"})
  public void findAllByProcessingStatus_defineSupplyStatus_expectResultWithGivenStatusOnly() {
    // Given
    List<DataUploadStatusHistoryEntity> dataUploadStatusHistoryEntityList = null;
    // When
    dataUploadStatusHistoryEntityList =
        dataUploadStatusHistoryRepository.findAllByProcessingStatus(ProcessingStatus.PENDING);
    // Then
    Assertions.assertFalse(dataUploadStatusHistoryEntityList.isEmpty());
    Assertions.assertEquals(2, dataUploadStatusHistoryEntityList.size());
    Assertions.assertEquals(
        ProcessingStatus.PENDING, dataUploadStatusHistoryEntityList.get(0).getProcessingStatus());
    Assertions.assertEquals(
        ProcessingStatus.PENDING, dataUploadStatusHistoryEntityList.get(1).getProcessingStatus());
  }
}
