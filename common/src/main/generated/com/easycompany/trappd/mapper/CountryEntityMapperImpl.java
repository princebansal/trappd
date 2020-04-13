package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.CountryDto;
import com.easycompany.trappd.model.dto.CountryDto.CountryDtoBuilder;
import com.easycompany.trappd.model.entity.CountryEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-13T17:35:36+0530",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_242 (Oracle Corporation)"
)
@Component
public class CountryEntityMapperImpl implements CountryEntityMapper {

    @Override
    public CountryDto toCountryDto(CountryEntity countryEntity) {
        if ( countryEntity == null ) {
            return null;
        }

        CountryDtoBuilder countryDto = CountryDto.builder();

        countryDto.code( countryEntity.getCode() );
        countryDto.name( countryEntity.getName() );

        return countryDto.build();
    }

    @Override
    public List<CountryDto> toCountryDtoList(List<CountryEntity> countryEntities) {
        if ( countryEntities == null ) {
            return null;
        }

        List<CountryDto> list = new ArrayList<CountryDto>( countryEntities.size() );
        for ( CountryEntity countryEntity : countryEntities ) {
            list.add( toCountryDto( countryEntity ) );
        }

        return list;
    }
}
