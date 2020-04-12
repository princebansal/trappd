package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.dto.CityDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityEntityMapper {

  CityDto toCityDto(CityEntity cityEntity);
  List<CityDto> toCityDtoList(List<CityEntity> cityEntities);
}
