package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.dto.CountryDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryEntityMapper {

  CountryDto toCountryDto(CountryEntity countryEntity);
  List<CountryDto> toCountryDtoList(List<CountryEntity> countryEntities);
}
