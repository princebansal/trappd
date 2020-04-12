package com.easycompany.trappd.model.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(name = "city")
@Cacheable
public class CityEntity extends AbstractBaseEntity {
  private String name;
  private String code;

  private String otherName;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "state_code", referencedColumnName = "code",nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private StateEntity state;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name =   "country_code", referencedColumnName = "code",nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private CountryEntity country;
}
