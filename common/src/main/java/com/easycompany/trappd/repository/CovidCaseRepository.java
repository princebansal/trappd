package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.StateEntity;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CovidCaseRepository extends JpaRepository<CovidCaseEntity, Long> {
  List<CovidCaseEntity> findAllByStatus(CaseStatus caseStatus);

  List<CovidCaseEntity> findAllByAnnouncedDateGreaterThanEqualAndCityOrderByAnnouncedDateDesc(
      LocalDate announcedDate, CityEntity cityEntity);

  List<CovidCaseEntity> findAllByCityOrderByAnnouncedDateDesc(CityEntity cityEntity);
  List<CovidCaseEntity> findAllByStateOrderByAnnouncedDateDesc(StateEntity stateEntity);
  List<CovidCaseEntity> findAllByCountryOrderByAnnouncedDateDesc(CountryEntity countryEntity);

  long countAllByStatusAndCity(CaseStatus caseStatus, CityEntity city);
  long countAllByCity(CityEntity city);

  long countAllByStatusAndState(CaseStatus caseStatus, StateEntity state);
  long countAllByState(StateEntity state);

  long countAllByStatusAndCountry(CaseStatus caseStatus, CountryEntity countryEntity);
  long countAllByCountry(CountryEntity countryEntity);

}
