package com.easycompany.trappd.service;

import com.easycompany.trappd.builder.GeoObjectFactory;
import com.easycompany.trappd.exception.BadRequestException;
import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.exception.StateNotFoundException;
import com.easycompany.trappd.mapper.CityEntityMapper;
import com.easycompany.trappd.mapper.CountryEntityMapper;
import com.easycompany.trappd.mapper.StateEntityMapper;
import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.GeographyType;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DashboardMoreInformationDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.DataTimelineDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import com.easycompany.trappd.model.dto.ThingsToDoCardDto;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import com.easycompany.trappd.model.dto.response.GetAllGeographicalEntitiesResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;
import com.easycompany.trappd.model.entity.AbstractBaseEntity;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.StateEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CountryRepository;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.repository.StateRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HomePageService {

  private final CountryRepository countryRepository;
  private final StateRepository stateRepository;
  private final CityRepository cityRepository;
  private final CovidCaseRepository covidCaseRepository;
  private final CountryEntityMapper countryEntityMapper;
  private final StateEntityMapper stateEntityMapper;
  private final CityEntityMapper cityEntityMapper;
  private final ObjectMapper objectMapper;
  private final GeoObjectFactory geoObjectFactory;

  @Autowired
  public HomePageService(CountryRepository countryRepository,
                         StateRepository stateRepository,
                         CityRepository cityRepository,
                         CovidCaseRepository covidCaseRepository,
                         CountryEntityMapper countryEntityMapper,
                         StateEntityMapper stateEntityMapper,
                         CityEntityMapper cityEntityMapper,
                         ObjectMapper objectMapper,
                         GeoObjectFactory geoObjectFactory) {
    this.countryRepository = countryRepository;
    this.stateRepository = stateRepository;
    this.cityRepository = cityRepository;
    this.covidCaseRepository = covidCaseRepository;
    this.countryEntityMapper = countryEntityMapper;
    this.stateEntityMapper = stateEntityMapper;
    this.cityEntityMapper = cityEntityMapper;
    this.objectMapper = objectMapper;
    this.geoObjectFactory = geoObjectFactory;
  }

  public GetAllCitiesResponse getListOfAllCitiesForCountry(String countryCode)
      throws CountryNotFoundException {

    CountryEntity countryEntity =
        countryRepository
            .findByCode(countryCode)
            .orElseThrow(
                () -> new CountryNotFoundException("Country not found with code " + countryCode));
    return GetAllCitiesResponse.builder()
        .countryDto(countryEntityMapper.toCountryDto(countryEntity))
        .cities(
            cityEntityMapper.toCityDtoList(
                countryEntity.getCities().stream()
                    .sorted(Comparator.comparing(CityEntity::getCode))
                    .collect(Collectors.toList())))
        .build();
  }

  public GetAllGeographicalEntitiesResponse getAllGeographicalEntities(String countryCode)
      throws CountryNotFoundException {

    List<CountryEntity> countryEntities = new ArrayList<>();
    CountryEntity countryEntity = countryRepository
        .findByCode(Objects.isNull(countryCode) ? "IN" : countryCode)
        .orElseThrow(() -> new CountryNotFoundException(
            "Country not found with code " + countryCode));

    if (Objects.isNull(countryCode)) {
      countryEntities.addAll(countryRepository.findAll());
    } else {
      countryEntities.add(countryEntity);
    }

    return GetAllGeographicalEntitiesResponse.builder()
        .countryDto(countryEntityMapper.toCountryDtoList(countryEntities))
        .states(stateEntityMapper.toStateDtoList(
            countryEntity.getStates().stream()
                .sorted(Comparator.comparing(StateEntity::getCode))
                .collect(Collectors.toList())))
        .cities(
            cityEntityMapper.toCityDtoList(
                countryEntity.getCities().stream()
                    .sorted(Comparator.comparing(CityEntity::getCode))
                    .collect(Collectors.toList())))
        .build();
  }

  public GetHomePageDataResponse getHomePageDataForCountryAndCity(
      String countryCode, String cityCode)
      throws CountryNotFoundException, CityNotFoundException, BadRequestException {
    CountryEntity countryEntity =
        countryRepository
            .findByCode(countryCode)
            .orElseThrow(
                () -> new CountryNotFoundException("Country not found with code " + countryCode));

    CityEntity cityEntity =
        cityRepository
            .findByCode(cityCode)
            .orElseThrow(() -> new CityNotFoundException("City not found with code " + cityCode));

    if (!cityEntity.getCountry().getCode().equalsIgnoreCase(countryCode)) {
      throw new BadRequestException("The city does not belong to given country");
    }

    List<CovidCaseEntity> caseEntities =
        covidCaseRepository.findAllByCityOrderByAnnouncedDateDesc(cityEntity);
    Map<String, List<CovidCaseEntity>> localDateListMap =
        caseEntities.stream()
            .sequential()
            .collect(
                Collectors.groupingBy(
                    covidCaseEntity ->
                        DateTimeUtil.formatLocalDate(
                            covidCaseEntity.getAnnouncedDate(), AppConstants.ANNOUNCED_DATE_FORMAT),
                    LinkedHashMap::new,
                    Collectors.toList()));

    long totalCases = covidCaseRepository.countAllByCity(cityEntity);
    long totalDeaths = covidCaseRepository.countAllByStatusAndCity(CaseStatus.DECEASED, cityEntity);
    long totalRecovered = covidCaseRepository.countAllByStatusAndCity(
        CaseStatus.RECOVERED, cityEntity);
    return GetHomePageDataResponse.builder()
        .city(cityEntityMapper.toCityDto(cityEntity))
        .country(countryEntityMapper.toCountryDto(countryEntity))
        .dashboard(
            DashboardDto.builder()
                .dataInsightsCard(
                    DataInsightsCardDto.builder()
                        .date(DateTimeUtil.todaysDateInUTCFormatted(AppConstants.UI_DATE_FORMAT))
                        .addItem(
                            DataInsightsDto.builder()
                                .title("Total Cases in " + cityEntity.getName())
                                .value(String.valueOf(totalCases))
                                .build())
                        .build())
                .thingsToDoCard(
                    ThingsToDoCardDto.builder()
                        .addItem("Play around")
                        .addItem("Do something")
                        .build())
                .moreInformation(
                    /*DashboardMoreInformationDto.builder()
                            .title("How to stay safe")
                            .heading(
                                "Washing your hands is the best way to help you stay safe. Here's how to do it")
                            .addInstruction(
                                "Wet your hands with clean, running water. Turn off the tap and apply soap.")
                            .addInstruction(
                                "Lather your hands by rubbing them together. Get the backs of your hands, between your fingers, and under your nails.")
                            .build())
                    .build()*/
                    getMoreInformation())
                .build())
        .detailedData(
            DetailedDataDto.builder()
                .addDataInsight(
                    DataInsightsDto.builder()
                        .title("Total Deaths")
                        .value(String.valueOf(totalDeaths))
                        .build())
                .addDataInsight(
                    DataInsightsDto.builder()
                        .title("Total Recovered")
                        .value(String.valueOf(totalRecovered))
                        .build())
                .timeline(
                    localDateListMap.entrySet().stream()
                        .filter(stringListEntry -> stringListEntry.getValue().size() > 0)
                        .limit(10)
                        .map(
                            stringListEntry -> {
                              LocalDate date =
                                  DateTimeUtil.parseLocalDate(
                                      stringListEntry.getKey(), AppConstants.ANNOUNCED_DATE_FORMAT);
                              String formattedDate = DateTimeUtil.formatLocalDate(date, "dd MMM");
                              int totalCasesForDay = stringListEntry.getValue().size();
                              long totalDeathsForDay =
                                  stringListEntry.getValue().stream()
                                      .filter(
                                          covidCaseEntity ->
                                              covidCaseEntity.getStatus() == CaseStatus.DECEASED)
                                      .count();
                              return DataTimelineDto.builder()
                                  .date(formattedDate)
                                  .day(
                                      date.getDayOfWeek()
                                          .getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                                  .title(totalCasesForDay + " total cases")
                                  .subtitle(totalDeathsForDay + " total deaths")
                                  .build();
                            })
                        .collect(Collectors.toList()))
                .build())
        .build();
  }

  private DashboardMoreInformationDto getMoreInformation() {
    try {
      return objectMapper.readValue(
          AppConstants.SAMPLE_MORE_INFORMATION, DashboardMoreInformationDto.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return DashboardMoreInformationDto.builder().build();
    }
  }

  public GetHomePageDataV2Response getHomePageDataByGeography(GeographyType geoType, String geoValue)
      throws CityNotFoundException, CountryNotFoundException, StateNotFoundException {
    return geoObjectFactory.getDashboardObjectBuilder(geoType).createDashBoardDto(geoValue);
  }
}
