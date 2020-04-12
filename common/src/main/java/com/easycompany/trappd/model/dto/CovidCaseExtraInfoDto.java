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
public class CovidCaseExtraInfoDto {

  private String statePatientNumber;

  private String notes;

  private String contactPatient;

  private String nationality;

  private String source1;

  private String source2;

  private String source3;

  private String backupNotes;
}
