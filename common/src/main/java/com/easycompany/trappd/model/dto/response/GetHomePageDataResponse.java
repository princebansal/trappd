package com.easycompany.trappd.model.dto.response;

import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CountryDto;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class GetHomePageDataResponse extends BaseResponse {

  private CountryDto country;
  private CityDto city;
  private DashboardDto dashboard;
  private DetailedDataDto detailedData;
}
