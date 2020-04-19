package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.StateDto;
import com.easycompany.trappd.model.entity.StateEntity;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * The interface State entity mapper.
 */
@Mapper(componentModel = "spring")
public interface StateEntityMapper {

  /**
   * To state dto state dto.
   *
   * @param stateEntity the state entity
   * @return the state dto
   */
  @Mappings({
      @Mapping(source = "country.name", target = "countryName"),
  })
  StateDto toStateDto(StateEntity stateEntity);

  /**
   * To city dto list list.
   *
   * @param stateEntities the state entities
   * @return the list
   */
  List<StateDto> toStateDtoList(List<StateEntity> stateEntities);

}
