package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.dto.CityDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CityEntityMapper {

  @Mappings({
      @Mapping(source = "state.name", target = "stateName"),
      @Mapping(source = "state.code", target = "stateCode"),
      @Mapping(source = "country.name", target = "countryName"),
  })
  CityDto toCityDto(CityEntity cityEntity);
  List<CityDto> toCityDtoList(List<CityEntity> cityEntities);
}
