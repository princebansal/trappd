package com.easycompany.trappd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Country dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {

  /**
   * The Code.
   */
  private String code;

  /**
   * The Name.
   */
  private String name;

}
