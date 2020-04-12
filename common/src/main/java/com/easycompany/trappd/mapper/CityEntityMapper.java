package com.easycompany.trappd.mapper;

import com.easycompany.trappd.entity.CityEntity;
import com.easycompany.trappd.entity.CountryEntity;
import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CountryDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityEntityMapper {

  CityDto toCityDto(CityEntity cityEntity);
  List<CityDto> toCityDtoList(List<CityEntity> cityEntities);
}
