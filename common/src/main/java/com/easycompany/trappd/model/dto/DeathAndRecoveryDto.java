package com.easycompany.trappd.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeathAndRecoveryDto {

  private String ageBracket;
  private String city;
  private String date;
  private String district;
  private String gender;

  private String nationality;
  private String notes;

  @JsonProperty("patientnumbercouldbemappedlater")
  private String patientNumber;

  @JsonProperty("patientstatus")
  private String status;

  @JsonProperty("slno")
  private String serialNumber;

  private String source1;
  private String source2;

  private String source3;

  private String state;

  @JsonProperty("statecode")
  private String stateCode;
}
