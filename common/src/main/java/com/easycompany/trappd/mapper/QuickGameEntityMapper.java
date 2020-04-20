package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.QuickGameDto;
import com.easycompany.trappd.model.dto.response.QuickGameDataResponse;
import com.easycompany.trappd.model.entity.QuickGameEntity;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuickGameEntityMapper {

  QuickGameDto toQuickGameDto(QuickGameEntity quickGameEntity);

  List<QuickGameDto> toQuickGameDtoList(List<QuickGameEntity> quickGameEntities);
}
