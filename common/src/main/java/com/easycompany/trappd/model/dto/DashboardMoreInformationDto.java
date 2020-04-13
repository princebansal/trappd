package com.easycompany.trappd.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMoreInformationDto {

  private String title;
  private String heading;
  @Singular("addInstruction")
  private List<String> instructions;
}
