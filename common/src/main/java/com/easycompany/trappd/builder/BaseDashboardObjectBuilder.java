package com.easycompany.trappd.builder;

import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.exception.StateNotFoundException;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseDashboardObjectBuilder {

  public abstract GetHomePageDataV2Response createDashBoardDto(String geoValue)
      throws CityNotFoundException, CountryNotFoundException, StateNotFoundException;

}
