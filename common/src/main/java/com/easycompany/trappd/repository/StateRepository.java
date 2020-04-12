package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.StateEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<StateEntity, Long> {

  Optional<StateEntity> findByCodeIgnoreCase(String code);

  List<StateEntity> findAllByCountry(CountryEntity countryEntity);
}
