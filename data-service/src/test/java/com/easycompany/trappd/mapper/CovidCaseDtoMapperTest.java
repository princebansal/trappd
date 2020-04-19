package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.constant.Gender;
import com.easycompany.trappd.model.dto.CaseDtoV2;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;
import com.easycompany.trappd.util.SampleDataSupplier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CovidCaseDtoMapperTest {

  @Autowired CovidCaseDtoMapper covidCaseDtoMapper;
  @Autowired ObjectMapper objectMapper;

  @Test
  public void toCovidCaseEntity_provideCaseDto_expectValidMappedResult()
      throws JsonProcessingException, ParseException {
    // Given
    CaseDtoV2 caseDtoV2 =
        objectMapper.readValue(SampleDataSupplier.sampleCaseDtoV2(), CaseDtoV2.class);
    // When
    CovidCaseEntity covidCaseEntity = covidCaseDtoMapper.toCovidCaseEntity(caseDtoV2);
    // Then
    Assertions.assertNotNull(covidCaseEntity);
    Assertions.assertEquals(null, covidCaseEntity.getId());
    Assertions.assertEquals(caseDtoV2.getPatientNumber(), covidCaseEntity.getPatientNumber());
    Assertions.assertEquals(
        DateTimeUtil.parseLocalDate(
            caseDtoV2.getDateAnnounced(), AppConstants.ANNOUNCED_DATE_FORMAT),
        covidCaseEntity.getAnnouncedDate());
    Assertions.assertNull(covidCaseEntity.getCity());
    Assertions.assertNull(covidCaseEntity.getState());
    Assertions.assertNull(covidCaseEntity.getCountry());
    Assertions.assertEquals(Integer.valueOf(caseDtoV2.getAgeBracket()), covidCaseEntity.getAge());
    Assertions.assertEquals(Gender.valueOf(caseDtoV2.getGender()), covidCaseEntity.getGender());
    Assertions.assertEquals(
        CovidCaseDtoMapper.stringToCaseStatusEnum(caseDtoV2.getCurrentStatus()),
        covidCaseEntity.getStatus());
    Assertions.assertNull(covidCaseEntity.getRecoveredDate());
    Assertions.assertNull(covidCaseEntity.getDeceasedDate());
    Assertions.assertEquals(
        CovidCaseDtoMapper.stringToTransmissionTypeEnum(caseDtoV2.getTypeOfTransmission()),
        covidCaseEntity.getTransmissionType());
    Assertions.assertNull(covidCaseEntity.getExtraInfo());
  }
}
