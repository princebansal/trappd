package com.easycompany.trappd.model.entity;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.Gender;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "death_and_recover_case")
@DynamicUpdate
public class DeathAndRecoverCaseEntity extends AbstractBaseEntity {

  private Integer age;

  @ManyToOne(fetch = FetchType.LAZY)
  private CityEntity city;

  private LocalDate date;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private String patientNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CaseStatus status;

  @Column(unique = true, nullable = false)
  private Long serialNumber;

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

  @Lob private byte[] extraInfo;
}
