package com.easycompany.trappd.model.dto.response;

import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CountryDto;
import com.easycompany.trappd.model.dto.StateDto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class GetAllGeographicalEntitiesResponse extends BaseResponse {

  /**
   * The Country dto.
   */
  private List<CountryDto> countryDto;

  /**
   * The State dto.
   */
  private List<StateDto> states;

  /**
   * The Cities.
   */
  @Singular("addCity")
  private List<CityDto> cities;
}
