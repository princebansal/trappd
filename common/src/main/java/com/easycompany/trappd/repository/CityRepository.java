package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

  List<CityEntity> findAllByCodeIgnoreCaseOrCodeIgnoreCase(String code1, String code2);

  List<CityEntity> findAllByCountry(CountryEntity countryEntity);

  Optional<CityEntity> findByCode(String cityCode);
}
