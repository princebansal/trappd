package com.easycompany.trappd.model.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class DashboardMoreInformationDto {

  private String title;
  private String heading;
  @Singular("addInstruction")
  private List<String> instructions;
}
