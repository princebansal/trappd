package com.easycompany.trappd.model.dto.response;

import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CountryDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class GetAllCitiesResponse {
  private CountryDto countryDto;
  @Singular("addCity")
  private List<CityDto> cities;
}
