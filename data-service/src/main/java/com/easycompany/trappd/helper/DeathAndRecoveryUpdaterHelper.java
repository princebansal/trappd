package com.easycompany.trappd.helper;

import com.easycompany.trappd.cache.CityCache;
import com.easycompany.trappd.cache.StateCache;
import com.easycompany.trappd.mapper.DeathAndRecoveryDtoMapper;
import com.easycompany.trappd.model.dto.DeathAndRecoveryCaseExtraInfoDto;
import com.easycompany.trappd.model.dto.DeathAndRecoveryDto;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.DeathAndRecoverCaseEntity;
import com.easycompany.trappd.model.entity.StateEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.DeathAndRecoveryCaseRepository;
import com.easycompany.trappd.repository.StateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DeathAndRecoveryUpdaterHelper
    extends DataUpdaterHelper<DeathAndRecoveryDto, DeathAndRecoverCaseEntity> {

  private final DeathAndRecoveryCaseRepository deathAndRecoveryCaseRepository;
  private final DeathAndRecoveryDtoMapper deathAndRecoveryDtoMapper;
  private final CityRepository cityRepository;
  private final StateRepository stateRepository;
  private final ObjectMapper objectMapper;
  private final CityCache cityCache;
  private final StateCache stateCache;

  @Autowired
  public DeathAndRecoveryUpdaterHelper(
      DeathAndRecoveryCaseRepository deathAndRecoveryCaseRepository,
      DeathAndRecoveryDtoMapper deathAndRecoveryDtoMapper,
      CityRepository cityRepository,
      StateRepository stateRepository,
      ObjectMapper objectMapper,
      CityCache cityCache,
      StateCache stateCache) {
    this.deathAndRecoveryCaseRepository = deathAndRecoveryCaseRepository;
    this.deathAndRecoveryDtoMapper = deathAndRecoveryDtoMapper;
    this.cityRepository = cityRepository;
    this.stateRepository = stateRepository;
    this.objectMapper = objectMapper;
    this.cityCache = cityCache;
    this.stateCache = stateCache;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void update(List<DeathAndRecoveryDto> latestCaseList) {
    log.debug("Starting update process for deaths and recovery cases");
    if (latestCaseList == null) {
      log.error("Returned deathsAndRecovery cases is null from S3/local file. Skipping update");
      return;
    }
    log.debug("Retrieving all cases from table [death_and_recovered_case]");
    List<DeathAndRecoverCaseEntity> listOfExistingRecords =
        deathAndRecoveryCaseRepository.findAll();
    if (listOfExistingRecords.size() == 0) {
      // Bulk insert all records
      log.debug("Bulk inserting all records as there is no history present");
      bulkInsert(latestCaseList);
    } else {
      // One to one check with hisotry and update records
      log.debug("Checking latest data against old records to update");
      checkAndUpdate(latestCaseList, listOfExistingRecords);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  void checkAndUpdate(
      List<DeathAndRecoveryDto> latestCaseList,
      List<DeathAndRecoverCaseEntity> listOfExistingRecords) {
    Map<Long, DeathAndRecoverCaseEntity> serialNumberDeathAndRecoverCaseMap =
        listOfExistingRecords.stream()
            .collect(
                Collectors.toMap(
                    deathAndRecoverCaseEntity -> deathAndRecoverCaseEntity.getSerialNumber(),
                    deathAndRecoverCaseEntity -> deathAndRecoverCaseEntity));

    List<DeathAndRecoveryDto> newCasesForBulkInsert = new ArrayList<>();
    List<DeathAndRecoverCaseEntity> oldCasesForBulkUpdate = new ArrayList<>();
    latestCaseList.stream()
        .forEach(
            deathAndRecoveryDto -> {
              if (!serialNumberDeathAndRecoverCaseMap.containsKey(
                  Long.parseLong(deathAndRecoveryDto.getSerialNumber()))) {
                newCasesForBulkInsert.add(deathAndRecoveryDto);
              } else {
                DeathAndRecoverCaseEntity deathAndRecoverCaseEntity =
                    serialNumberDeathAndRecoverCaseMap.get(
                        Long.parseLong(deathAndRecoveryDto.getSerialNumber()));

                if (shouldUpdate(deathAndRecoveryDto, deathAndRecoverCaseEntity)) {
                  oldCasesForBulkUpdate.add(deathAndRecoverCaseEntity);
                }
              }
            });

    if (newCasesForBulkInsert.size() > 0) {
      // Bulk update new cases
      log.debug("Bulk inserting new records");
      bulkInsert(newCasesForBulkInsert);
    } else {
      log.debug("There are no records to insert");
    }
    if (oldCasesForBulkUpdate.size() > 0) {
      // Bulk update new cases
      log.debug("Bulk updating new records");
      bulkUpdate(oldCasesForBulkUpdate);
    } else {
      log.debug("There are no records to update");
    }
    onUpdateSuccess("Raw data update process finished successfully");
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  void bulkUpdate(List<DeathAndRecoverCaseEntity> recordsToUpdate) {
    // deathAndRecoveryCaseRepository.saveAll(recordsToUpdate);
    log.debug("bulkUpdate() called");
    log.debug("Bulk updated {} records successfully", recordsToUpdate.size());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  void bulkInsert(List<DeathAndRecoveryDto> recordsToInsert) {
    log.debug("bulkInsert() called");
    List<DeathAndRecoverCaseEntity> deathAndRecoverCaseEntities =
        recordsToInsert.stream()
            .filter(caseDto -> !StringUtils.isEmpty(caseDto.getStateCode()))
            .map(
                deathAndRecoveryDto -> {
                  DeathAndRecoverCaseEntity deathAndRecoverCaseEntity =
                      deathAndRecoveryDtoMapper.toDeathAndRecoveryCaseEntity(deathAndRecoveryDto);
                  if (deathAndRecoverCaseEntity == null) {
                    log.error("deathAndRecoverCaseEntity is null");
                  }

                  // Check if City code is null
                  CityEntity cityEntity = null;
                  /*if (deathAndRecoveryDto.getCity() != null
                      && deathAndRecoveryDto.getDistrict() != null) {
                    List<CityEntity> cityEntities =
                        cityRepository.findAllByCodeIgnoreCaseOrCodeIgnoreCase(
                            deathAndRecoveryDto.getCity(), deathAndRecoveryDto.getDistrict());
                    cityEntity = cityEntities.stream().findFirst().orElse(null);
                  }*/
                  cityEntity =
                      cityCache.getCityEntity(
                          deathAndRecoveryDto.getCity() + deathAndRecoveryDto.getStateCode(),
                          deathAndRecoveryDto.getDistrict() + deathAndRecoveryDto.getStateCode());
                  deathAndRecoverCaseEntity.setCity(cityEntity);
                  StateEntity stateEntity =
                      cityEntity != null
                          ? cityEntity.getState()
                          : stateCache
                              .getStateEntity(deathAndRecoveryDto.getStateCode())
                              .orElse(null);

                  if (stateEntity == null) {
                    return null;
                  }
                  CountryEntity countryEntity = stateEntity.getCountry();

                  if (cityEntity == null) {
                    // log.trace("City is null for [{}]", deathAndRecoveryDto.getSerialNumber());
                  }
                  if (countryEntity == null) {
                    log.trace("Country is null for [{}]", deathAndRecoveryDto.getSerialNumber());
                  }
                  deathAndRecoverCaseEntity.setState(stateEntity);
                  deathAndRecoverCaseEntity.setCountry(countryEntity);
                  deathAndRecoverCaseEntity.setExtraInfo(getExtraInfo(deathAndRecoveryDto));
                  return deathAndRecoverCaseEntity;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    log.trace("Lenth of records to be inserted {}", deathAndRecoverCaseEntities.size());
    deathAndRecoveryCaseRepository.saveAll(deathAndRecoverCaseEntities);
    log.debug("Bulk inserted {} records successfully", deathAndRecoverCaseEntities.size());
  }

  @Override
  boolean shouldUpdate(DeathAndRecoveryDto dto, DeathAndRecoverCaseEntity entity) {
    if (!StringUtils.isEmpty(dto.getPatientNumber())
        && (StringUtils.isEmpty(entity.getPatientNumber())
            || (!dto.getPatientNumber().equals(entity.getPatientNumber())))) {
      log.trace(
          "Patient number changed for serial number {} from {} to {}",
          entity.getSerialNumber(),
          entity.getPatientNumber(),
          dto.getPatientNumber());
      entity.setPatientNumber(dto.getPatientNumber());
      return true;
    }
    return false;
  }

  private byte[] getExtraInfo(DeathAndRecoveryDto deathAndRecoveryDto) {
    try {
      return objectMapper
          .writeValueAsString(
              DeathAndRecoveryCaseExtraInfoDto.builder()
                  .nationality(deathAndRecoveryDto.getNationality())
                  .notes(deathAndRecoveryDto.getNotes())
                  .source1(deathAndRecoveryDto.getSource1())
                  .source2(deathAndRecoveryDto.getSource2())
                  .source3(deathAndRecoveryDto.getSource3())
                  .build())
          .getBytes();
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      return null;
    }
  }
}
