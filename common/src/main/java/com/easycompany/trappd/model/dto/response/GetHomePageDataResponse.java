package com.easycompany.trappd.model.dto.response;

import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CountryDto;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetHomePageDataResponse {

  private CountryDto country;
  private CityDto city;
  private DashboardDto dashboard;
  private DetailedDataDto detailedData;
}
