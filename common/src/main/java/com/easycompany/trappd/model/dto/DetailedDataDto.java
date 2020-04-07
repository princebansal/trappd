package com.easycompany.trappd.model.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class DetailedDataDto {

  @Singular("addDataInsight")
  private List<DataInsightsDto> dataInsights;
  @Singular("addTimelinePhase")
  private List<DataTimelineDto> timeline;
}
