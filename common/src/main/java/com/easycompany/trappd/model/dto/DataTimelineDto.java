package com.easycompany.trappd.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataTimelineDto {

  private String date;
  private String day;
  private String title;
  private String subtitle;
}
