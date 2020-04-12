package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.CaseDto;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity.CovidCaseEntityBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-13T04:02:38+0530",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_242 (Oracle Corporation)"
)
@Component
public class CovidCaseDtoMapperImpl implements CovidCaseDtoMapper {

    @Override
    public CovidCaseEntity toCovidCaseEntity(CaseDto caseDto) {
        if ( caseDto == null ) {
            return null;
        }

        CovidCaseEntityBuilder covidCaseEntity = CovidCaseEntity.builder();

        covidCaseEntity.gender( CovidCaseDtoMapper.stringToGenderEnum( caseDto.getGender() ) );
        covidCaseEntity.transmissionType( CovidCaseDtoMapper.stringToTransmissionTypeEnum( caseDto.getTypeOfTransmission() ) );
        covidCaseEntity.age( CovidCaseDtoMapper.stringToInteger( caseDto.getAgeBracket() ) );
        covidCaseEntity.status( CovidCaseDtoMapper.stringToCaseStatusEnum( caseDto.getCurrentStatus() ) );
        covidCaseEntity.patientNumber( caseDto.getPatientNumber() );

        covidCaseEntity.announcedDate( emptyToNull(caseDto.getDateAnnounced()) );

        return covidCaseEntity.build();
    }

    @Override
    public List<CovidCaseEntity> toCovidCaseEntityList(List<CaseDto> caseDtoList) {
        if ( caseDtoList == null ) {
            return null;
        }

        List<CovidCaseEntity> list = new ArrayList<CovidCaseEntity>( caseDtoList.size() );
        for ( CaseDto caseDto : caseDtoList ) {
            list.add( toCovidCaseEntity( caseDto ) );
        }

        return list;
    }
}
