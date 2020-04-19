package com.easycompany.trappd.builder;

import com.easycompany.trappd.exception.StateNotFoundException;
import com.easycompany.trappd.mapper.CountryEntityMapper;
import com.easycompany.trappd.mapper.StateEntityMapper;
import com.easycompany.trappd.model.constant.GeographyType;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.ThingsToDoCardDto;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;
import com.easycompany.trappd.model.entity.StateEntity;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.repository.StateRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;

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
            .thingsToDoCard(
                ThingsToDoCardDto.builder()
                    .addItem("Play around")
                    .addItem("Do something")
                    .build())
            .build())
        .build();
  }
}
