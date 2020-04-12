package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CityDto.CityDtoBuilder;
import com.easycompany.trappd.model.entity.CityEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-12T16:38:32+0530",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_242 (Oracle Corporation)"
)
@Component
public class CityEntityMapperImpl implements CityEntityMapper {

    @Override
    public CityDto toCityDto(CityEntity cityEntity) {
        if ( cityEntity == null ) {
            return null;
        }

        CityDtoBuilder cityDto = CityDto.builder();

        cityDto.code( cityEntity.getCode() );
        cityDto.name( cityEntity.getName() );

        return cityDto.build();
    }

    @Override
    public List<CityDto> toCityDtoList(List<CityEntity> cityEntities) {
        if ( cityEntities == null ) {
            return null;
        }

        List<CityDto> list = new ArrayList<CityDto>( cityEntities.size() );
        for ( CityEntity cityEntity : cityEntities ) {
            list.add( toCityDto( cityEntity ) );
        }

        return list;
    }
}
