package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.DeathAndRecoverCaseEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeathAndRecoveryCaseRepository extends JpaRepository<DeathAndRecoverCaseEntity, Long> {
  List<CovidCaseEntity> findAllByStatus(CaseStatus caseStatus);
  long countAllByStatusAndCity(CaseStatus caseStatus, CityEntity city);
  long countAllByCity(CityEntity city);

}
