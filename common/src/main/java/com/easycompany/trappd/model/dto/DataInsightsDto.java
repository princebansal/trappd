package com.easycompany.trappd.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataInsightsDto {
  private String title;
  private String value;
}
