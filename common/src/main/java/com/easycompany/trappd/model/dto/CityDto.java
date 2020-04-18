package com.easycompany.trappd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type City dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityDto {

  /**
   * The Code.
   */
  private String code;

  /**
   * The Name.
   */
  private String name;

  /**
   * The State code.
   */
  private String stateCode;

  /**
   * The Country code.
   */
  private String countryCode;

}
