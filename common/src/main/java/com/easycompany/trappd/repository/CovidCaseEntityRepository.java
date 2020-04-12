package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.entity.CountryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
  Optional<CountryEntity> findByCode(String countryCode);
}
