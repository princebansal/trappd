package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.Gender;
import com.easycompany.trappd.model.constant.TransmissionType;
import com.easycompany.trappd.model.dto.CaseDto;
import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
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
    @Mapping(target = "announcedDate", source = "dateAnnounced", dateFormat = "dd/MM/yyyy"),
  })
  CovidCaseEntity toCovidCaseEntity(CaseDto caseDto);

  List<CovidCaseEntity> toCovidCaseEntityList(List<CaseDto> caseDtoList);

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
    switch (s) {
      case "Imported":
        return TransmissionType.IMPORTED;
      case "Local":
        return TransmissionType.LOCAL;
      default:
        return TransmissionType.UNKNOWN;
    }
  }
}
