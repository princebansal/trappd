package com.easycompany.trappd.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@MappedSuperclass
public abstract class AbstractBaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  @NotNull
  @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
  private Date createdAt;

  @NotNull
  @UpdateTimestamp
  @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
  private Date updatedAt;
}
