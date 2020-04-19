package com.easycompany.trappd.builder;

import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.mapper.CountryEntityMapper;
import com.easycompany.trappd.model.constant.GeographyType;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.ThingsToDoCardDto;
import com.easycompany.trappd.model.dto.response.GetHomePageDataV2Response;
import com.easycompany.trappd.model.entity.CountryEntity;
import com.easycompany.trappd.repository.CountryRepository;
import com.easycompany.trappd.repository.CovidCaseRepository;
import com.easycompany.trappd.util.AppConstants;
import com.easycompany.trappd.util.DateTimeUtil;

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
        () -> new CountryNotFoundException("city not found with code " + geoValue));


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
            .thingsToDoCard(
                ThingsToDoCardDto.builder()
                    .addItem("Play around")
                    .addItem("Do something")
                    .build())
            .build())
        .build();
  }
}