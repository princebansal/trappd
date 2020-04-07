package com.easycompany.trappd.repository;

import com.easycompany.trappd.entity.CityEntity;
import com.easycompany.trappd.entity.CountryEntity;
import com.easycompany.trappd.model.dto.CityDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class CityRepositoryTest {

  @Autowired private CityRepository cityRepository;

  @Test
  @Sql({"classpath:/datasets/country.sql", "classpath:/datasets/city.sql"})
  public void findAll_retrieveAllCities_ExpectNonZeroLengthList() {
    // Given
    List<CityEntity> cityEntities = null;

    CountryEntity countryEntity = CountryEntity.builder().code("IN").name("India").flag("").build();
    List<CityEntity> expectedCities = new ArrayList<>();

    expectedCities.add(
        CityEntity.builder().code("BLR").name("Bangalore").country(countryEntity).build());
    expectedCities.add(
        CityEntity.builder().code("BOM").name("Mumbai").country(countryEntity).build());
    expectedCities.add(
        CityEntity.builder().code("DEL").name("Delhi").country(countryEntity).build());
    expectedCities.add(
        CityEntity.builder().code("CCU").name("Kolkata").country(countryEntity).build());
    // When
    cityEntities = cityRepository.findAll();
    // Then
    Assertions.assertNotNull(cityEntities);
    Assertions.assertTrue(cityEntities.size() > 0);
    Assertions.assertEquals(4, cityEntities.size());
  }
}
