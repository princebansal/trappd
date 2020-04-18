package com.easycompany.trappd.service;

import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.dto.DashboardMoreInformationDto;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.exception.BadRequestException;
import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.mapper.CityEntityMapper;
import com.easycompany.trappd.mapper.CountryEntityMapper;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.DataTimelineDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import com.easycompany.trappd.model.dto.ThingsToDoCardDto;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataResponse;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CountryRepository;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomePageService {

  private final CountryRepository countryRepository;
  private final CityRepository cityRepository;
  private final CovidCaseRepository covidCaseRepository;
  private final CountryEntityMapper countryEntityMapper;
  private final CityEntityMapper cityEntityMapper;
  private final ObjectMapper objectMapper;

  @Autowired
  public HomePageService(
      CountryRepository countryRepository,
      CityRepository cityRepository,
      CovidCaseRepository covidCaseRepository,
      CountryEntityMapper countryEntityMapper,
      CityEntityMapper cityEntityMapper,
      ObjectMapper objectMapper) {
    this.countryRepository = countryRepository;
    this.cityRepository = cityRepository;
    this.covidCaseRepository = covidCaseRepository;
    this.countryEntityMapper = countryEntityMapper;
    this.cityEntityMapper = cityEntityMapper;
    this.objectMapper = objectMapper;
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
    String todaysDate = DateTimeUtil.todaysDateInUTCFormatted(AppConstants.ANNOUNCED_DATE_FORMAT);
    long totalCasesToday =
        localDateListMap.containsKey(todaysDate) ? localDateListMap.get(todaysDate).size() : 0;
    long totalDeathsToday =
        localDateListMap.containsKey(todaysDate)
            ? localDateListMap.get(todaysDate).stream()
                .filter(covidCaseEntity -> covidCaseEntity.getStatus() == CaseStatus.DECEASED)
                .count()
            : 0;
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
                                .title("Cases today in " + cityEntity.getName())
                                .value(String.valueOf(totalCasesToday))
                                .build())
                        .addItem(
                            DataInsightsDto.builder()
                                .title("Deaths today in " + cityEntity.getName())
                                .value(String.valueOf(totalDeathsToday))
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
                        .title("Total Cases")
                        .value(String.valueOf(totalCases))
                        .build())
                .addDataInsight(
                    DataInsightsDto.builder()
                        .title("Total Deaths")
                        .value(String.valueOf(totalDeaths))
                        .build())
                .timeline(
                    localDateListMap.entrySet().stream()
                        .filter(stringListEntry -> stringListEntry.getValue().size() > 0)
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
                        .limit(10)
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
}
