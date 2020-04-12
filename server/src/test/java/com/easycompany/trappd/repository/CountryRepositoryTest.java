package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.entity.CountryEntity;
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
class CountryRepositoryTest {

  @Autowired private CountryRepository countryRepository;

  @Test
  @Sql({"classpath:/datasets/cityStateCountry.sql"})
  public void findAll_retrieveAllCountries_ExpectNonZeroLengthList() {
    // Given
    List<CountryEntity> countryEntities = null;
    CountryEntity countryEntity =
        CountryEntity.builder()
            .code("IN")
            .name("India")
            .flag("")
            .cities(Collections.emptySet())
            .build();
    // When
    countryEntities = countryRepository.findAll();
    // Then
    Assertions.assertNotNull(countryEntities);
    Assertions.assertTrue(countryEntities.size() > 0);
    Assertions.assertEquals(1, countryEntities.size());
    Assertions.assertEquals(countryEntity, countryEntities.get(0));
  }
}
