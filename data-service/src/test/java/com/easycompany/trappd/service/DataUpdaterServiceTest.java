package com.easycompany.trappd.service;

import com.easycompany.trappd.exception.DataUpdaterServiceException;
import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.dto.CaseDtoV2;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.DataUploadStatusHistoryEntity;
import com.easycompany.trappd.repository.DataUploadStatusHistoryRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class DataUpdaterServiceTest {

  @Autowired DataUpdaterService dataUpdaterService;
  @Autowired DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository;

  @Test
  @Sql({"classpath:/datasets/engine_service_test1.sql"})
  public void update_noRowsInDataUploadStatusHistoryEntity_throwException() {
    // Given
    // When
    // Then
    Assertions.assertThrows(DataUpdaterServiceException.class, () -> dataUpdaterService.update());
  }

  @Test
  @Sql({"classpath:/datasets/engine_service_test2.sql"})
  public void update_onlyProcessingDataUploadStatusHistoryEntity_expectNoChangeInDatabase()
      throws IOException, DataUpdaterServiceException {
    // Given
    // When
    dataUpdaterService.update();
    Optional<DataUploadStatusHistoryEntity> uploadStatusHistoryEntity =
        dataUploadStatusHistoryRepository.findFirstByOrderByUploadDateDesc();
    // Then
    Assertions.assertTrue(uploadStatusHistoryEntity.isPresent());
    Assertions.assertEquals(
        ProcessingStatus.PROCESSING, uploadStatusHistoryEntity.get().getProcessingStatus());
  }

  @Test
  public void checkDateAnnounced_checkForSameDate_expectReturnFalse() {
    // Given
    LocalDate localDate = LocalDate.of(2020, 4, 13);
    CovidCaseEntity covidCaseEntity = CovidCaseEntity.builder().announcedDate(localDate).build();
    CaseDtoV2 caseDtoV2 = CaseDtoV2.builder().dateAnnounced("13/04/2020").build();
    // When
    boolean shouldUpdate = dataUpdaterService.checkDateAnnounced(caseDtoV2, covidCaseEntity);
    // Then
    Assertions.assertFalse(shouldUpdate);
  }

  @Test
  public void checkCaseStatus_checkForSameStatus_expectReturnFalse() {
    // Given

    CovidCaseEntity covidCaseEntity = CovidCaseEntity.builder().status(CaseStatus.DECEASED).build();
    CaseDtoV2 caseDtoV2 = CaseDtoV2.builder().currentStatus("Deceased").build();
    // When
    boolean shouldUpdate = dataUpdaterService.checkCaseStatus(caseDtoV2, covidCaseEntity);
    // Then
    Assertions.assertFalse(shouldUpdate);
  }
}
