package com.easycompany.trappd.model.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class DataInsightsCardDto {

  private String date;
  @Singular("addItem")
  private List<DataInsightsDto> items;
}
