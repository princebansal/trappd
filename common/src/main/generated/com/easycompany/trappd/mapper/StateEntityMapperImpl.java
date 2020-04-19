package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.StateDto;
import com.easycompany.trappd.model.dto.StateDto.StateDtoBuilder;
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
public class StateEntityMapperImpl implements StateEntityMapper {

    @Override
    public StateDto toStateDto(StateEntity stateEntity) {
        if ( stateEntity == null ) {
            return null;
        }

        StateDtoBuilder stateDto = StateDto.builder();

        stateDto.countryName( stateEntityCountryName( stateEntity ) );
        stateDto.code( stateEntity.getCode() );
        stateDto.name( stateEntity.getName() );

        return stateDto.build();
    }

    @Override
    public List<StateDto> toStateDtoList(List<StateEntity> stateEntities) {
        if ( stateEntities == null ) {
            return null;
        }

        List<StateDto> list = new ArrayList<StateDto>( stateEntities.size() );
        for ( StateEntity stateEntity : stateEntities ) {
            list.add( toStateDto( stateEntity ) );
        }

        return list;
    }

    private String stateEntityCountryName(StateEntity stateEntity) {
        if ( stateEntity == null ) {
            return null;
        }
        CountryEntity country = stateEntity.getCountry();
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
