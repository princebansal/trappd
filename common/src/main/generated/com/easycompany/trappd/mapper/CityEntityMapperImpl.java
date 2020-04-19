package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CityDto.CityDtoBuilder;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.StateEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-19T19:09:15+0530",
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

        cityDto.stateName( cityEntityStateName( cityEntity ) );
        cityDto.countryName( cityEntityCountryName( cityEntity ) );
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

    private String cityEntityStateName(CityEntity cityEntity) {
        if ( cityEntity == null ) {
            return null;
        }
        StateEntity state = cityEntity.getState();
        if ( state == null ) {
            return null;
        }
        String name = state.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String cityEntityCountryName(CityEntity cityEntity) {
        if ( cityEntity == null ) {
            return null;
        }
        CountryEntity country = cityEntity.getCountry();
        if ( country == null ) {
            return null;
        }
        String name = country.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
