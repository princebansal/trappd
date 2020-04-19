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
public class CaseDtoV2 {

  @JsonProperty("patientnumber")
  private String patientNumber;

  @JsonProperty("statepatientnumber")
  private String statePatientNumber;

  @JsonProperty("dateannounced")
  private String dateAnnounced;

  @JsonProperty("agebracket")
  private String ageBracket;

  @JsonProperty("gender")
  private String gender;

  @JsonProperty("detectedcity")
  private String detectedCity;

  @JsonProperty("detecteddistrict")
  private String detectedDistrict;

  @JsonProperty("detectedstate")
  private String detectedState;

  @JsonProperty("statecode")
  private String stateCode;

  @JsonProperty("currentstatus")
  private String currentStatus;

  @JsonProperty("notes")
  private String notes;

  @JsonProperty("contractedfromwhichpatientsuspected")
  private String contactPatient;

  @JsonProperty("nationality")
  private String nationality;

  @JsonProperty("typeoftransmission")
  private String typeOfTransmission;

  @JsonProperty("statuschangedate")
  private String statusChangeDate;

  @JsonProperty("source1")
  private String source1;

  @JsonProperty("source2")
  private String source2;

  @JsonProperty("source3")
  private String source3;

  @JsonProperty("backupnotes")
  private String backupNotes;
}
