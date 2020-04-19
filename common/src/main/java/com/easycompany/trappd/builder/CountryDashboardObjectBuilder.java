package com.easycompany.trappd.builder;

import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.mapper.CountryEntityMapper;
import com.easycompany.trappd.model.constant.CaseStatus;
import com.easycompany.trappd.model.constant.GeographyType;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.DataTimelineDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import com.easycompany.trappd.model.dto.ThingsToDoCardDto;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.repository.CountryRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountryDashboardObjectBuilder extends BaseDashboardObjectBuilder {

  private final CovidCaseRepository covidCaseRepository;
  private final CountryRepository countryRepository;
  private final CountryEntityMapper countryEntityMapper;

  @Autowired
  public CountryDashboardObjectBuilder(final CovidCaseRepository covidCaseRepository,
                                       final CountryRepository countryRepository,
                                       final CountryEntityMapper countryEntityMapper) {
    this.covidCaseRepository = covidCaseRepository;
    this.countryRepository = countryRepository;
    this.countryEntityMapper = countryEntityMapper;
  }


  @Override
  public GetHomePageDataV2Response createDashBoardDto(String geoValue)
      throws CountryNotFoundException {

    CountryEntity countryEntity = countryRepository.findByCode(geoValue).orElseThrow(
        () -> new CountryNotFoundException("country not found with code " + geoValue));

    long totalDeaths = covidCaseRepository.countAllByStatusAndCountry(CaseStatus.DECEASED, countryEntity);
    long totalRecovered = covidCaseRepository.countAllByStatusAndCountry(
        CaseStatus.RECOVERED, countryEntity);

    List<CovidCaseEntity> caseEntities =
        covidCaseRepository.findAllByCountryOrderByAnnouncedDateDesc(countryEntity);
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
        .geographyType(GeographyType.COUNTRY)
        .country(countryEntityMapper.toCountryDto(countryEntity))
        .dashboard(DashboardDto.builder()
            .dataInsightsCard(
                DataInsightsCardDto.builder()
                    .date(DateTimeUtil.todaysDateInUTCFormatted(AppConstants.UI_DATE_FORMAT))
                    .addItem(
                        DataInsightsDto.builder()
                            .title("Total Cases in " + countryEntity.getName())
                            .value(String.valueOf(covidCaseRepository.countAllByCountry(countryEntity)))
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
}