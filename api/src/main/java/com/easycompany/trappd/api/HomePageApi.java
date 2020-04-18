package com.easycompany.trappd.api;

import com.easycompany.trappd.exception.BadRequestException;
import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import com.easycompany.trappd.model.dto.response.GetAllGeographicalEntitiesResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataResponse;
import org.springframework.http.ResponseEntity;

public interface HomePageApi {
  ResponseEntity<GetAllCitiesResponse> getAllCities(String countryCode)
      throws CountryNotFoundException;

  ResponseEntity<GetAllGeographicalEntitiesResponse> getAllGeographicalEntities(String countryCode)
      throws CountryNotFoundException;

  ResponseEntity<GetHomePageDataResponse> getHomePageData(String countryCode,
                                                          String stateCode,
                                                          String cityCode)
      throws BadRequestException, CountryNotFoundException, CityNotFoundException;
}
