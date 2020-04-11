package com.easycompany.trappd.model.dto.response;

import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CountryDto;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class GetAllCitiesResponse extends BaseResponse {
  private CountryDto countryDto;

  @Singular("addCity")
  private List<CityDto> cities;
}
