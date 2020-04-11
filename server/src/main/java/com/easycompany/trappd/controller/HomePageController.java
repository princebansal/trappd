package com.easycompany.trappd.controller;

import com.easycompany.trappd.api.HomePageApi;
import com.easycompany.trappd.exception.BadRequestException;
import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataResponse;
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

  @GetMapping("/getHomePageData")
  @Override
  public ResponseEntity<GetHomePageDataResponse> getHomePageData(
      String countryCode, String cityCode)
      throws BadRequestException, CountryNotFoundException, CityNotFoundException {
    return ResponseEntity.ok(
        homePageService.getHomePageDataForCountryAndCity(countryCode, cityCode));
  }
}
