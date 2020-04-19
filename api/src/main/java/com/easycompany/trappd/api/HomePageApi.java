package com.easycompany.trappd.api;

import com.easycompany.trappd.exception.BadRequestException;
import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.exception.StateNotFoundException;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import com.easycompany.trappd.model.dto.response.GetAllGeographicalEntitiesResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;

import org.springframework.http.ResponseEntity;

public interface HomePageApi {
  ResponseEntity<GetAllCitiesResponse> getAllCities(String countryCode)
      throws CountryNotFoundException;

  ResponseEntity<GetAllGeographicalEntitiesResponse> getAllGeographicalEntities(String countryCode)
      throws CountryNotFoundException;

  ResponseEntity<GetHomePageDataResponse> getHomePageData(String countryCode,
                                                          String cityCode)
      throws BadRequestException, CountryNotFoundException, CityNotFoundException;

  ResponseEntity<GetHomePageDataV2Response> getHomePageDataV2(String geoType,
                                                              String geoValue)
      throws BadRequestException, CountryNotFoundException, CityNotFoundException, StateNotFoundException;
}
