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
public class CaseDto {

  @JsonProperty("Patient Number")
  private String patientNumber;

  @JsonProperty("State Patient Number")
  private String statePatientNumber;

  @JsonProperty("Date Announced")
  private String dateAnnounced;

  @JsonProperty("Age Bracket")
  private String ageBracket;

  @JsonProperty("Gender")
  private String gender;

  @JsonProperty("Detected City")
  private String detectedCity;

  @JsonProperty("Detected District")
  private String detectedDistrict;

  @JsonProperty("Detected State")
  private String detectedState;

  @JsonProperty("State code")
  private String stateCode;

  @JsonProperty("Current Status")
  private String currentStatus;

  @JsonProperty("Notes")
  private String notes;

  @JsonProperty("Contracted from which Patient (Suspected)")
  private String contactPatient;

  @JsonProperty("Nationality")
  private String nationality;

  @JsonProperty("Type of transmission")
  private String typeOfTransmission;

  @JsonProperty("Status Change Date")
  private String statusChangeDate;

  @JsonProperty("Source_1")
  private String source1;

  @JsonProperty("Source_2")
  private String source2;

  @JsonProperty("Source_3")
  private String source3;

  @JsonProperty("Backup Notes")
  private String backupNotes;
}
