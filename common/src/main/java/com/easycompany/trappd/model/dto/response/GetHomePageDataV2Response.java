package com.easycompany.trappd.model.dto.response;

import com.easycompany.trappd.model.constant.GeographyType;
import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CountryDto;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import com.easycompany.trappd.model.dto.StateDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class GetHomePageDataV2Response extends BaseResponse {

  private GeographyType geographyType;

  private CountryDto country;
  private StateDto state;
  private CityDto city;

  private DashboardDto dashboard;
  private DetailedDataDto detailedData;
}
