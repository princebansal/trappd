package com.easycompany.trappd.model.entity;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.Gender;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.constant.TransmissionType;
import java.time.LocalDate;
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
@Table(name = "covid_case")
public class CovidCaseEntity extends AbstractBaseEntity {

  @Column(nullable = false, unique = true)
  private String patientNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  private CityEntity city;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "state_code", referencedColumnName = "code", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private StateEntity state;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_code", referencedColumnName = "code", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private CountryEntity country;

  private Integer age;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CaseStatus status;

  private LocalDate recoveredDate;

  private LocalDate deceasedDate;

  @Column
  private LocalDate announcedDate;

  @Enumerated(EnumType.STRING)
  private TransmissionType transmissionType;

  private byte[] extraInfo;
}
