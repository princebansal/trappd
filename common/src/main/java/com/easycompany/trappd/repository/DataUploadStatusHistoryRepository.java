package com.easycompany.trappd.repository;

import com.easycompany.trappd.entity.DataUploadStatusHistoryEntity;
import java.util.Optional;
import java.util.Spliterator.OfInt;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataUploadStatusHistoryRepository
    extends JpaRepository<DataUploadStatusHistoryEntity, Long> {
    Optional<DataUploadStatusHistoryEntity> findFirstByOrderByUploadDateDesc();
}
