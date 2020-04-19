package com.easycompany.trappd.builder;

import com.easycompany.trappd.cache.CityCache;
import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.exception.StateNotFoundException;
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
import com.easycompany.trappd.model.entity.DeathAndRecoverCaseEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.repository.DeathAndRecoveryCaseRepository;
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
public class CityDashboardObjectBuilder extends BaseDashboardObjectBuilder {

  private final CityRepository cityRepository;
  private final CovidCaseRepository covidCaseRepository;
  private final DeathAndRecoveryCaseRepository deathAndRecoveryCaseRepository;
  private final CityEntityMapper cityEntityMapper;
  private final StateEntityMapper stateEntityMapper;
  private final CountryEntityMapper countryEntityMapper;
  private final CityCache cityCache;

  @Autowired
  public CityDashboardObjectBuilder(
      CityRepository cityRepository,
      CovidCaseRepository covidCaseRepository,
      DeathAndRecoveryCaseRepository deathAndRecoveryCaseRepository,
      CityEntityMapper cityEntityMapper,
      StateEntityMapper stateEntityMapper,
      CountryEntityMapper countryEntityMapper,
      CityCache cityCache) {
    this.cityRepository = cityRepository;
    this.covidCaseRepository = covidCaseRepository;
    this.deathAndRecoveryCaseRepository = deathAndRecoveryCaseRepository;
    this.cityEntityMapper = cityEntityMapper;
    this.stateEntityMapper = stateEntityMapper;
    this.countryEntityMapper = countryEntityMapper;
    this.cityCache = cityCache;
  }

  @Override
  public GetHomePageDataV2Response createDashBoardDto(String geoValue)
      throws CityNotFoundException {

    CityEntity cityEntity =
        cityCache
            .getCityEntity(geoValue)
            .orElseThrow(() -> new CityNotFoundException("city not found with code " + geoValue));

    long totalDeaths =
        deathAndRecoveryCaseRepository.countAllByStatusAndCity(CaseStatus.DECEASED, cityEntity);
    long totalRecovered =
        deathAndRecoveryCaseRepository.countAllByStatusAndCity(CaseStatus.RECOVERED, cityEntity);

    List<CovidCaseEntity> caseEntities =
        covidCaseRepository.findAllByCityOrderByAnnouncedDateDesc(cityEntity);

    List<DeathAndRecoverCaseEntity> deathAndRecoverCaseEntities =
        deathAndRecoveryCaseRepository.findAllByCityOrderByDateDesc(cityEntity);

    Map<String, List<CovidCaseEntity>> localDateToCovidCaseListMap =
        caseEntities.stream()
            .sequential()
            .collect(
                Collectors.groupingBy(
                    covidCaseEntity ->
                        DateTimeUtil.formatLocalDate(
                            covidCaseEntity.getAnnouncedDate(), AppConstants.ANNOUNCED_DATE_FORMAT),
                    LinkedHashMap::new,
                    Collectors.toList()));

    Map<String, List<DeathAndRecoverCaseEntity>> localDateToDeathAndRecoverListMap =
        deathAndRecoverCaseEntities.stream()
            .sequential()
            .collect(
                Collectors.groupingBy(
                    deathAndRecoverCaseEntity ->
                        DateTimeUtil.formatLocalDate(
                            deathAndRecoverCaseEntity.getDate(),
                            AppConstants.ANNOUNCED_DATE_FORMAT),
                    LinkedHashMap::new,
                    Collectors.toList()));

    return GetHomePageDataV2Response.builder()
        .geographyType(GeographyType.CITY)
        .city(cityEntityMapper.toCityDto(cityEntity))
        .state(stateEntityMapper.toStateDto(cityEntity.getState()))
        .country(countryEntityMapper.toCountryDto(cityEntity.getCountry()))
        .dashboard(
            DashboardDto.builder()
                .dataInsightsCard(
                    DataInsightsCardDto.builder()
                        .date(DateTimeUtil.todaysDateInUTCFormatted(AppConstants.UI_DATE_FORMAT))
                        .addItem(
                            DataInsightsDto.builder()
                                .title("Total Cases in " + cityEntity.getName())
                                .value(String.valueOf(caseEntities.size()))
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
                .timeline(
                    localDateToCovidCaseListMap.entrySet().stream()
                        .filter(stringListEntry -> stringListEntry.getValue().size() > 0)
                        .limit(10)
                        .map(
                            stringListEntry -> {
                              LocalDate date =
                                  DateTimeUtil.parseLocalDate(
                                      stringListEntry.getKey(), AppConstants.ANNOUNCED_DATE_FORMAT);
                              String formattedDate =
                                  DateTimeUtil.formatLocalDate(date, AppConstants.UI_DATE_FORMAT);
                              int totalCasesForDay = stringListEntry.getValue().size();
                              long totalDeathsForDay =
                                  localDateToDeathAndRecoverListMap.containsKey(
                                          stringListEntry.getKey())
                                      ? localDateToDeathAndRecoverListMap
                                          .get(stringListEntry.getKey()).stream()
                                          .filter(
                                              deathAndRecoverCaseEntity ->
                                                  deathAndRecoverCaseEntity.getStatus()
                                                      == CaseStatus.DECEASED)
                                          .count()
                                      : 0L;
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
