package com.easycompany.trappd.controller;

import com.easycompany.trappd.api.HomePageApi;
import com.easycompany.trappd.exception.BadRequestException;
import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.exception.StateNotFoundException;
import com.easycompany.trappd.model.constant.GeographyType;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import com.easycompany.trappd.model.dto.response.GetAllGeographicalEntitiesResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;
import com.easycompany.trappd.model.dto.response.QuickGameDataResponse;
import com.easycompany.trappd.service.HomePageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@CrossOrigin
public class HomePageController implements HomePageApi {

  private final HomePageService homePageService;

  @Autowired
  public HomePageController(HomePageService homePageService) {
    this.homePageService = homePageService;
  }

  @GetMapping("/getAllCities")
  @Override
  public ResponseEntity<GetAllCitiesResponse> getAllCities(String countryCode)
      throws CountryNotFoundException {
    return ResponseEntity.ok(homePageService.getListOfAllCitiesForCountry(countryCode));
  }

  @GetMapping("/getAllGeo")
  @Override
  public ResponseEntity<GetAllGeographicalEntitiesResponse> getAllGeographicalEntities(String countryCode)
      throws CountryNotFoundException {
    return ResponseEntity.ok(homePageService.getAllGeographicalEntities(countryCode));
  }

  @GetMapping("/getHomePageData")
  @Override
  public ResponseEntity<GetHomePageDataResponse> getHomePageData(
      String countryCode, String cityCode)
      throws BadRequestException, CountryNotFoundException, CityNotFoundException {
    return ResponseEntity.ok(
        homePageService.getHomePageDataForCountryAndCity(countryCode, cityCode));
  }

  @GetMapping("/v2/getHomePageData")
  @Override
  public ResponseEntity<GetHomePageDataV2Response> getHomePageDataV2(String geoType, String geoValue)
      throws BadRequestException, CityNotFoundException, CountryNotFoundException, StateNotFoundException {
    return ResponseEntity.ok(
        homePageService.getHomePageDataByGeography(GeographyType.getEnumByName(geoType), geoValue));
  }

  @GetMapping("/fetchQuickGameData")
  @Override
  public ResponseEntity<QuickGameDataResponse> getQuickGamesData() {
    return ResponseEntity.ok(
        homePageService.getQuickGameData());
  }
}
