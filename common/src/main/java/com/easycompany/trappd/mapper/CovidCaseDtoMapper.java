package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.Gender;
import com.easycompany.trappd.model.constant.TransmissionType;
import com.easycompany.trappd.model.dto.CaseDtoV2;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;
import java.time.LocalDate;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CovidCaseDtoMapper {
  @Mappings({
    @Mapping(source = "ageBracket", target = "age", qualifiedByName = "stringToInteger"),
    @Mapping(source = "gender", target = "gender", qualifiedByName = "stringToGenderEnum"),
    @Mapping(
        source = "currentStatus",
        target = "status",
        qualifiedByName = "stringToCaseStatusEnum"),
    @Mapping(
        source = "typeOfTransmission",
        target = "transmissionType",
        qualifiedByName = "stringToTransmissionTypeEnum"),
    @Mapping(target = "extraInfo", ignore = true),
    @Mapping(target = "city", ignore = true),
    @Mapping(target = "state", ignore = true),
    @Mapping(target = "country", ignore = true),
    @Mapping(target = "recoveredDate", ignore = true),
    @Mapping(target = "deceasedDate", ignore = true),
    @Mapping(
        target = "announcedDate",
        expression = "java(parseDate(caseDtoV2.getDateAnnounced()))"),
  })
  CovidCaseEntity toCovidCaseEntity(CaseDtoV2 caseDtoV2);

  List<CovidCaseEntity> toCovidCaseEntityList(List<CaseDtoV2> caseDtoV2List);

  @Named("stringToInteger")
  static Integer stringToInteger(String s) {
    try {
      return Integer.valueOf(s);
    } catch (Exception e) {
      return null;
    }
  }

  @Named("stringToGenderEnum")
  static Gender stringToGenderEnum(String s) {
    try {
      return Gender.valueOf(s);
    } catch (Exception e) {
      return null;
    }
  }

  @Named("stringToCaseStatusEnum")
  static CaseStatus stringToCaseStatusEnum(String s) {
    if (s == null) {
      return CaseStatus.ACTIVE;
    }
    switch (s) {
      case "Recovered":
        return CaseStatus.RECOVERED;
      case "Deceased":
        return CaseStatus.DECEASED;
      case "Hospitalized":
      default:
        return CaseStatus.ACTIVE;
    }
  }

  @Named("stringToTransmissionTypeEnum")
  static TransmissionType stringToTransmissionTypeEnum(String s) {
    if (s == null) {
      return TransmissionType.UNKNOWN;
    }
    switch (s) {
      case "Imported":
        return TransmissionType.IMPORTED;
      case "Local":
        return TransmissionType.LOCAL;
      default:
        return TransmissionType.UNKNOWN;
    }
  }

  default LocalDate parseDate(String s) {
    if (s == null || s.isEmpty()) return null;
    return DateTimeUtil.parseLocalDate(s, AppConstants.ANNOUNCED_DATE_FORMAT);
  }
}
