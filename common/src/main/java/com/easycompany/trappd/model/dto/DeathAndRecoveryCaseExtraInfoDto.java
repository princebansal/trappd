package com.easycompany.trappd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeathAndRecoveryCaseExtraInfoDto {

  private String notes;

  private String nationality;

  private String source1;

  private String source2;

  private String source3;
}
