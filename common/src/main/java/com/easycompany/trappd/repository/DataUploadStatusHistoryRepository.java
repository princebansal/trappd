package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.entity.DataUploadStatusHistoryEntity;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataUploadStatusHistoryRepository
    extends JpaRepository<DataUploadStatusHistoryEntity, Long> {
  Optional<DataUploadStatusHistoryEntity> findFirstByOrderByUploadDateDesc();

  List<DataUploadStatusHistoryEntity> findByOrderByUploadDateDesc(Pageable pageable);

  Optional<DataUploadStatusHistoryEntity> findFirstByAndProcessingStatusEqualsOrderByUploadDateDesc(
      ProcessingStatus status);

  List<DataUploadStatusHistoryEntity> findAllByProcessingStatus(ProcessingStatus status);
}
