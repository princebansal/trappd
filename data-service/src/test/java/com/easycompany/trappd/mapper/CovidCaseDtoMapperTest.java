package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.Gender;
import com.easycompany.trappd.model.dto.CaseDto;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.util.SampleDataSupplier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    CaseDto caseDto = objectMapper.readValue(SampleDataSupplier.sampleCaseDto(), CaseDto.class);
    // When
    CovidCaseEntity covidCaseEntity = covidCaseDtoMapper.toCovidCaseEntity(caseDto);
    // Then
    Assertions.assertNotNull(covidCaseEntity);
    Assertions.assertEquals(null, covidCaseEntity.getId());
    Assertions.assertEquals(caseDto.getPatientNumber(), covidCaseEntity.getPatientNumber());
    Assertions.assertEquals(
        LocalDate.parse(caseDto.getDateAnnounced(), DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        covidCaseEntity.getAnnouncedDate());
    Assertions.assertNull(covidCaseEntity.getCity());
    Assertions.assertNull(covidCaseEntity.getState());
    Assertions.assertNull(covidCaseEntity.getCountry());
    Assertions.assertEquals(Integer.valueOf(caseDto.getAgeBracket()), covidCaseEntity.getAge());
    Assertions.assertEquals(Gender.valueOf(caseDto.getGender()), covidCaseEntity.getGender());
    Assertions.assertEquals(
        CovidCaseDtoMapper.stringToCaseStatusEnum(caseDto.getCurrentStatus()),
        covidCaseEntity.getStatus());
    Assertions.assertNull(covidCaseEntity.getRecoveredDate());
    Assertions.assertNull(covidCaseEntity.getDeceasedDate());
    Assertions.assertEquals(
        CovidCaseDtoMapper.stringToTransmissionTypeEnum(caseDto.getTypeOfTransmission()),
        covidCaseEntity.getTransmissionType());
    Assertions.assertNull(covidCaseEntity.getExtraInfo());
  }
}
