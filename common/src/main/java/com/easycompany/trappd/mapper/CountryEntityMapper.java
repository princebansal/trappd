package com.easycompany.trappd.mapper;

import com.easycompany.trappd.entity.CountryEntity;
import com.easycompany.trappd.model.dto.CountryDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CountryEntityMapper {

  CountryDto toCountryDto(CountryEntity countryEntity);
  List<CountryDto> toCountryDtoList(List<CountryEntity> countryEntities);
}
