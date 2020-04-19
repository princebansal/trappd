package com.easycompany.trappd.builder;

import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.mapper.CityEntityMapper;
import com.easycompany.trappd.mapper.CountryEntityMapper;
import com.easycompany.trappd.mapper.StateEntityMapper;
import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.GeographyType;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.DataTimelineDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CityDashboardObjectBuilder extends BaseDashboardObjectBuilder{

  private final CityRepository cityRepository;
  private final CovidCaseRepository covidCaseRepository;
  private final CityEntityMapper cityEntityMapper;
  private final StateEntityMapper stateEntityMapper;
  private final CountryEntityMapper countryEntityMapper;

  @Autowired
  public CityDashboardObjectBuilder(CityRepository cityRepository,
                                    CovidCaseRepository covidCaseRepository,
                                    CityEntityMapper cityEntityMapper,
                                    StateEntityMapper stateEntityMapper,
                                    CountryEntityMapper countryEntityMapper) {
    this.cityRepository = cityRepository;
    this.covidCaseRepository = covidCaseRepository;
    this.cityEntityMapper = cityEntityMapper;
    this.stateEntityMapper = stateEntityMapper;
    this.countryEntityMapper = countryEntityMapper;
  }


  @Override
  public GetHomePageDataV2Response createDashBoardDto(String geoValue) throws CityNotFoundException {


    CityEntity cityEntity = cityRepository
        .findByCode(geoValue)
        .orElseThrow(
            () -> new CityNotFoundException("city not found with code " + geoValue));

    long totalDeaths = covidCaseRepository.countAllByStatusAndCity(CaseStatus.DECEASED, cityEntity);
    long totalRecovered = covidCaseRepository.countAllByStatusAndCity(
        CaseStatus.RECOVERED, cityEntity);

    long totalCases = covidCaseRepository.countAllByCity(cityEntity);

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

    return GetHomePageDataV2Response.builder()
        .geographyType(GeographyType.CITY)
        .city(cityEntityMapper.toCityDto(cityEntity))
        .state(stateEntityMapper.toStateDto(cityEntity.getState()))
        .country(countryEntityMapper.toCountryDto(cityEntity.getCountry()))
        .dashboard(DashboardDto.builder()
            .dataInsightsCard(
                DataInsightsCardDto.builder()
                    .date(DateTimeUtil.todaysDateInUTCFormatted(AppConstants.UI_DATE_FORMAT))
                    .addItem(
                        DataInsightsDto.builder()
                            .title("Total Cases in " + cityEntity.getName())
                            .value(String.valueOf(totalCases))
                            .build())
                    .build())
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
                .timeline(localDateListMap.entrySet().stream()
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
}
