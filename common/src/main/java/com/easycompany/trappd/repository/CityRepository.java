package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

  Optional<CityEntity> findByCode(String code);

  List<CityEntity> findAllByCountry(CountryEntity countryEntity);
}
