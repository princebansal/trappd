package com.easycompany.trappd.mapper;

import com.easycompany.trappd.model.dto.DeathAndRecoveryDto;
import com.easycompany.trappd.model.entity.DeathAndRecoverCaseEntity;
import com.easycompany.trappd.model.entity.DeathAndRecoverCaseEntity.DeathAndRecoverCaseEntityBuilder;
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
public class DeathAndRecoveryDtoMapperImpl implements DeathAndRecoveryDtoMapper {

    @Override
    public DeathAndRecoverCaseEntity toDeathAndRecoveryCaseEntity(DeathAndRecoveryDto deathAndRecoveryDto) {
        if ( deathAndRecoveryDto == null ) {
            return null;
        }

        DeathAndRecoverCaseEntityBuilder deathAndRecoverCaseEntity = DeathAndRecoverCaseEntity.builder();

        deathAndRecoverCaseEntity.gender( DeathAndRecoveryDtoMapper.stringToGenderEnum( deathAndRecoveryDto.getGender() ) );
        deathAndRecoverCaseEntity.age( DeathAndRecoveryDtoMapper.stringToInteger( deathAndRecoveryDto.getAgeBracket() ) );
        deathAndRecoverCaseEntity.status( DeathAndRecoveryDtoMapper.stringToCaseStatusEnum( deathAndRecoveryDto.getStatus() ) );
        deathAndRecoverCaseEntity.patientNumber( deathAndRecoveryDto.getPatientNumber() );
        if ( deathAndRecoveryDto.getSerialNumber() != null ) {
            deathAndRecoverCaseEntity.serialNumber( Long.parseLong( deathAndRecoveryDto.getSerialNumber() ) );
        }

        deathAndRecoverCaseEntity.date( parseDate(deathAndRecoveryDto.getDate()) );

        return deathAndRecoverCaseEntity.build();
    }

    @Override
    public List<DeathAndRecoverCaseEntity> toDeathAndRecoveryCaseEntityList(List<DeathAndRecoveryDto> deathAndRecoveryDtoList) {
        if ( deathAndRecoveryDtoList == null ) {
            return null;
        }

        List<DeathAndRecoverCaseEntity> list = new ArrayList<DeathAndRecoverCaseEntity>( deathAndRecoveryDtoList.size() );
        for ( DeathAndRecoveryDto deathAndRecoveryDto : deathAndRecoveryDtoList ) {
            list.add( toDeathAndRecoveryCaseEntity( deathAndRecoveryDto ) );
        }

        return list;
    }
}
