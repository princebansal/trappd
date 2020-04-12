package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CovidCaseEntityRepository extends JpaRepository<CovidCaseEntity, Long> {
  List<CovidCaseEntity> findAllByStatus(CaseStatus caseStatus);
}
