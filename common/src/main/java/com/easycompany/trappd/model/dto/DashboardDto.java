package com.easycompany.trappd.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDto {

  private DataInsightsCardDto dataInsightsCard;
  private ThingsToDoCardDto thingsToDoCard;
  private DashboardMoreInformationDto moreInformation;
}
