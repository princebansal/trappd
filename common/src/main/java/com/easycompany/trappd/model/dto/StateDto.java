package com.easycompany.trappd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type State dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateDto {

  /**
   * The Code.
   */
  private String code;

  /**
   * The Name.
   */
  private String name;

  /**
   * The Country name.
   */
  private String countryName;

}
