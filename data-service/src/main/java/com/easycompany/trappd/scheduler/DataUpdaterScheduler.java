package com.easycompany.trappd.scheduler;

import com.easycompany.trappd.entity.DataUploadStatusHistoryEntity;
import com.easycompany.trappd.exception.DataUploadStatusHistoryEntityNotFoundException;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.repository.DataUploadStatusHistoryRepository;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
@Slf4j
public class DataUpdaterScheduler {

  private final DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository;

  @Autowired
  public DataUpdaterScheduler(DataUploadStatusHistoryRepository dataUploadStatusHistoryRepository) {
    this.dataUploadStatusHistoryRepository = dataUploadStatusHistoryRepository;
  }

  @SneakyThrows
  @Scheduled(fixedDelay = 2000)
  private void update() {
    DataUploadStatusHistoryEntity dataUploadStatusHistoryEntity = dataUploadStatusHistoryRepository
        .findFirstByOrderByUploadDateDesc().orElseThrow(()-> new DataUploadStatusHistoryEntityNotFoundException("No records present in the database"));
    if(dataUploadStatusHistoryEntity.getProcessingStatus()== ProcessingStatus.PENDING){
      startProcessingUpdate(dataUploadStatusHistoryEntity);
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  protected void startProcessingUpdate(DataUploadStatusHistoryEntity dataUploadStatusHistoryEntity) {

  }
}
