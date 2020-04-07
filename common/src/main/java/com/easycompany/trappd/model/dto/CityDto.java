package com.easycompany.trappd.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityDto {
  private String code;
  private String name;
}
