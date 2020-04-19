package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.CaseDtoV2;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity.CovidCaseEntityBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-19T19:14:34+0530",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_242 (Oracle Corporation)"
)
@Component
public class CovidCaseDtoMapperImpl implements CovidCaseDtoMapper {

    @Override
    public CovidCaseEntity toCovidCaseEntity(CaseDtoV2 caseDtoV2) {
        if ( caseDtoV2 == null ) {
            return null;
        }

        CovidCaseEntityBuilder covidCaseEntity = CovidCaseEntity.builder();

        covidCaseEntity.gender( CovidCaseDtoMapper.stringToGenderEnum( caseDtoV2.getGender() ) );
        covidCaseEntity.transmissionType( CovidCaseDtoMapper.stringToTransmissionTypeEnum( caseDtoV2.getTypeOfTransmission() ) );
        covidCaseEntity.age( CovidCaseDtoMapper.stringToInteger( caseDtoV2.getAgeBracket() ) );
        covidCaseEntity.status( CovidCaseDtoMapper.stringToCaseStatusEnum( caseDtoV2.getCurrentStatus() ) );
        covidCaseEntity.patientNumber( caseDtoV2.getPatientNumber() );

        covidCaseEntity.announcedDate( parseDate(caseDtoV2.getDateAnnounced()) );

        return covidCaseEntity.build();
    }

    @Override
    public List<CovidCaseEntity> toCovidCaseEntityList(List<CaseDtoV2> caseDtoV2List) {
        if ( caseDtoV2List == null ) {
            return null;
        }

        List<CovidCaseEntity> list = new ArrayList<CovidCaseEntity>( caseDtoV2List.size() );
        for ( CaseDtoV2 caseDtoV2 : caseDtoV2List ) {
            list.add( toCovidCaseEntity( caseDtoV2 ) );
        }

        return list;
    }
}
