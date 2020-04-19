package com.easycompany.trappd.builder;

import com.easycompany.trappd.exception.StateNotFoundException;
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
import com.easycompany.trappd.model.entity.CovidCaseEntity;
import com.easycompany.trappd.model.entity.StateEntity;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.repository.StateRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class StateDashboardObjectBuilder extends BaseDashboardObjectBuilder {

  private final StateRepository stateRepository;
  private final StateEntityMapper stateEntityMapper;
  private final CountryEntityMapper countryEntityMapper;
  private final CovidCaseRepository covidCaseRepository;

  public StateDashboardObjectBuilder(StateRepository stateRepository,
                                     StateEntityMapper stateEntityMapper,
                                     CountryEntityMapper countryEntityMapper,
                                     CovidCaseRepository covidCaseRepository) {
    this.stateRepository = stateRepository;
    this.stateEntityMapper = stateEntityMapper;
    this.countryEntityMapper = countryEntityMapper;
    this.covidCaseRepository = covidCaseRepository;
  }

  @Override
  public GetHomePageDataV2Response createDashBoardDto(String geoValue) throws StateNotFoundException {

    StateEntity stateEntity = stateRepository.findByCodeIgnoreCase(geoValue)
        .orElseThrow(() -> new StateNotFoundException("state not found with code " + geoValue));

    long totalDeaths = covidCaseRepository.countAllByStatusAndState(CaseStatus.DECEASED, stateEntity);
    long totalRecovered = covidCaseRepository.countAllByStatusAndState(
        CaseStatus.RECOVERED, stateEntity);

    List<CovidCaseEntity> caseEntities =
        covidCaseRepository.findAllByStateOrderByAnnouncedDateDesc(stateEntity);
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
        .geographyType(GeographyType.STATE)
        .state(stateEntityMapper.toStateDto(stateEntity))
        .country(countryEntityMapper.toCountryDto(stateEntity.getCountry()))
        .dashboard(DashboardDto.builder()
            .dataInsightsCard(DataInsightsCardDto.builder()
                .date(DateTimeUtil.todaysDateInUTCFormatted(AppConstants.UI_DATE_FORMAT))
                .addItem(DataInsightsDto.builder()
                    .title("Total Cases in " + stateEntity.getName())
                    .value(String.valueOf(covidCaseRepository.countAllByState(stateEntity)))
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
