package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  @Sql({"classpath:/datasets/cityStateCountry.sql"})
  public void findAll_retrieveAllCities_ExpectNonZeroLengthList() {
    // Given
    List<CityEntity> cityEntities = null;

    CountryEntity countryEntity = CountryEntity.builder().code("IN").name("India").flag("").build();
    List<CityEntity> expectedCities = new ArrayList<>();

    // When
    cityEntities = cityRepository.findAll();
    // Then
    Assertions.assertNotNull(cityEntities);
    Assertions.assertTrue(cityEntities.size() > 0);
    Assertions.assertEquals(4, cityEntities.size());
  }
  @Test
  @Sql({"classpath:/datasets/cityStateCountry.sql"})
  public void findByCodeIgnoreCaseOrCodeIgnoreCase_retrieveCityByCode_expectValidResult() {
    // Given
    List<CityEntity> cityEntity = null;
    // When
    cityEntity = cityRepository.findAllByCodeIgnoreCaseOrCodeIgnoreCase("nicobars","nicobars");
    // Then
    Assertions.assertNotNull(cityEntity);
    Assertions.assertEquals(1, cityEntity.size());
    Assertions.assertEquals(1, cityEntity.get(0).getId());
  }
}
