package com.easycompany.trappd.entity;

import com.easycompany.trappd.model.constant.ProcessingStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "data_upload_status_history")
public class DataUploadStatusHistoryEntity extends AbstractBaseEntity {

  @Column(length = 2046, nullable = false)
  private String filePath;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProcessingStatus processingStatus;

  @Column(nullable = false)
  private LocalDateTime uploadDate;
}
