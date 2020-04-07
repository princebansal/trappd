package com.easycompany.trappd.service;

import static org.junit.jupiter.api.Assertions.*;

import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.model.dto.CountryDto;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class HomePageServiceTest {

  @Autowired HomePageService homePageService;

  @Test
  @Sql({"classpath:/datasets/country.sql", "classpath:/datasets/city.sql"})
  void getListOfAllCitiesForCountry_giveValidCountry_expectNonZeroCities()
      throws CountryNotFoundException {
    // Given
    CountryDto countryDto= CountryDto.builder()
        .name("India")
        .code("IN")
        .build();
    GetAllCitiesResponse getAllCitiesResponse = null;
    // When
    getAllCitiesResponse = homePageService.getListOfAllCitiesForCountry(countryDto.getCode());
    // Then
    Assertions.assertDoesNotThrow(
        () -> {
          homePageService.getListOfAllCitiesForCountry(countryDto.getCode());
        });
    Assertions.assertEquals(countryDto,getAllCitiesResponse.getCountryDto());

  }
}
