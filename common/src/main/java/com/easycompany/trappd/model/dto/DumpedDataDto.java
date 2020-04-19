package com.easycompany.trappd.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class DumpedDataDto {

  @Singular("addRawData")
  @JsonProperty("rawData")
  private List<CaseDtoV2> caseDtoV2List;

  @Singular("addDeathAndRecoveryDto")
  @JsonProperty("deathsAndRecoveries")
  private List<DeathAndRecoveryDto> deathAndRecoveryDtoList;
}
