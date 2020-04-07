package com.easycompany.trappd.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "city")
public class CityEntity extends AbstractBaseEntity {
  private String name;
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_code", referencedColumnName = "code")
  @NotNull
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private CountryEntity country;
}
