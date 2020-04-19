package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.Gender;
import com.easycompany.trappd.model.dto.DeathAndRecoveryDto;
import com.easycompany.trappd.model.entity.DeathAndRecoverCaseEntity;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;
import java.time.LocalDate;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DeathAndRecoveryDtoMapper {
  @Mappings({
    @Mapping(source = "ageBracket", target = "age", qualifiedByName = "stringToInteger"),
    @Mapping(source = "gender", target = "gender", qualifiedByName = "stringToGenderEnum"),
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToCaseStatusEnum"),
    @Mapping(target = "extraInfo", ignore = true),
    @Mapping(target = "city", ignore = true),
    @Mapping(target = "state", ignore = true),
    @Mapping(target = "country", ignore = true),
    @Mapping(target = "date", expression = "java(parseDate(deathAndRecoveryDto.getDate()))"),
  })
  DeathAndRecoverCaseEntity toDeathAndRecoveryCaseEntity(DeathAndRecoveryDto deathAndRecoveryDto);

  List<DeathAndRecoverCaseEntity> toDeathAndRecoveryCaseEntityList(
      List<DeathAndRecoveryDto> deathAndRecoveryDtoList);

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

  default LocalDate parseDate(String s) {
    if (s == null || s.isEmpty()) return null;
    return DateTimeUtil.parseLocalDate(s, AppConstants.ANNOUNCED_DATE_FORMAT);
  }
}
