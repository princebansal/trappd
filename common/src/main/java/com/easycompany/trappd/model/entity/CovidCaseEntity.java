package com.easycompany.trappd.model.entity;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.Gender;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.constant.TransmissionType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "case")
public class CaseEntity extends AbstractBaseEntity {

  @Column(nullable = false, unique = true)
  private String patientNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "city_code", referencedColumnName = "code", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private CityEntity city;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "state_code", referencedColumnName = "code", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private StateEntity stateEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_code", referencedColumnName = "code", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private CountryEntity countryEntity;

  private Integer age;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Enumerated(EnumType.STRING)
  private CaseStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProcessingStatus processingStatus;

  @Column(nullable = false)
  private LocalDateTime recoveredDate;

  @Column(nullable = false)
  private LocalDateTime deceasedDate;

  @Column(nullable = false)
  private LocalDateTime announcedDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransmissionType transmissionType;

  @Column(nullable = false)
  private String extraInfo;
}
