package com.easycompany.trappd.model.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class ThingsToDoCardDto {

  @Singular("addItem")
  private List<String> items;
}
