package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.ProcessingStatus;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.DataUploadStatusHistoryEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class CovidCaseEntityRepositoryTest {

  @Autowired private CovidCaseEntityRepository covidCaseEntityRepository;

  @Test
  @Tag("covid_case_test1")
  @Sql({"classpath:/datasets/covid_case_test1.sql"})
  public void findFirstByOrderByUploadDateDesc_supplySampleDataSet_expectLastRow() {
    // Given
    List<CovidCaseEntity> covidCaseEntities = null;
    // When
    covidCaseEntities = covidCaseEntityRepository.findAllByStatus(CaseStatus.ACTIVE);
    // Then
    Assertions.assertFalse(covidCaseEntities.isEmpty());
    Assertions.assertEquals(2, covidCaseEntities.size());
  }
}
