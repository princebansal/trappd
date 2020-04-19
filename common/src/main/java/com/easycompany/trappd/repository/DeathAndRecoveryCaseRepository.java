package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.entity.AbstractBaseEntity;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.DeathAndRecoverCaseEntity;
import com.easycompany.trappd.model.entity.StateEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeathAndRecoveryCaseRepository
    extends JpaRepository<DeathAndRecoverCaseEntity, Long> {
  List<DeathAndRecoverCaseEntity> findAllByStatus(CaseStatus caseStatus);

  long countAllByStatusAndCity(CaseStatus caseStatus, CityEntity city);

  long countAllByCity(CityEntity city);

  List<DeathAndRecoverCaseEntity> findAllByCityOrderByDateDesc(CityEntity cityEntity);
  List<DeathAndRecoverCaseEntity> findAllByStateOrderByDateDesc(StateEntity stateEntity);

  long countAllByStatusAndState(CaseStatus caseStatus, StateEntity stateEntity);

  long countAllByStatusAndCountry(CaseStatus ca, CountryEntity countryEntity);

  List<DeathAndRecoverCaseEntity> findAllByCountryOrderByDateDesc(CountryEntity countryEntity);
}
