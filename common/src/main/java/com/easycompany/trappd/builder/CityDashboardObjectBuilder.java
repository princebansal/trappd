package com.easycompany.trappd.builder;

import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.mapper.CityEntityMapper;
import com.easycompany.trappd.mapper.CountryEntityMapper;
import com.easycompany.trappd.mapper.StateEntityMapper;
import com.easycompany.trappd.model.constant.GeographyType;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.ThingsToDoCardDto;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;
import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;

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


    long totalCases = covidCaseRepository.countAllByCity(cityEntity);

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
            .thingsToDoCard(
                ThingsToDoCardDto.builder()
                    .addItem("Play around")
                    .addItem("Do something")
                    .build())
            .build())
        .build();
  }
}
