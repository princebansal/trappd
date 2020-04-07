package com.easycompany.trappd.service;

import com.easycompany.trappd.entity.CityEntity;
import com.easycompany.trappd.entity.CountryEntity;
import com.easycompany.trappd.exception.BadRequestException;
import com.easycompany.trappd.exception.CityNotFoundException;
import com.easycompany.trappd.exception.CountryNotFoundException;
import com.easycompany.trappd.mapper.CityEntityMapper;
import com.easycompany.trappd.mapper.CountryEntityMapper;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DashboardMoreInformationDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.DataTimelineDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import com.easycompany.trappd.model.dto.ThingsToDoCardDto;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataResponse;
import com.easycompany.trappd.repository.CityRepository;
import com.easycompany.trappd.repository.CountryRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomePageService {

  private final CountryRepository countryRepository;
  private final CityRepository cityRepository;
  private final CountryEntityMapper countryEntityMapper;
  private final CityEntityMapper cityEntityMapper;

  @Autowired
  public HomePageService(
      CountryRepository countryRepository,
      CityRepository cityRepository,
      CountryEntityMapper countryEntityMapper,
      CityEntityMapper cityEntityMapper) {
    this.countryRepository = countryRepository;
    this.cityRepository = cityRepository;
    this.countryEntityMapper = countryEntityMapper;
    this.cityEntityMapper = cityEntityMapper;
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
        .cities(cityEntityMapper.toCityDtoList(new ArrayList(countryEntity.getCities())))
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
            .orElseThrow(
                () -> new CityNotFoundException("City not found with code " + cityCode));

    if(!cityEntity.getCountry().getCode().equalsIgnoreCase(countryCode)){
      throw new BadRequestException("The city does not belong to given country");
    }
    return
        GetHomePageDataResponse.builder()
            .city(cityEntityMapper.toCityDto(cityEntity))
            .country(countryEntityMapper.toCountryDto(countryEntity))
            .dashboard(
                DashboardDto.builder()
                    .dataInsightsCard(
                        DataInsightsCardDto.builder()
                            .date("5th April")
                            .addItem(
                                DataInsightsDto.builder().title("Total cases").value("20").build())
                            .addItem(
                                DataInsightsDto.builder().title("Total Deaths").value("20").build())
                            .build())
                    .thingsToDoCard(
                        ThingsToDoCardDto.builder()
                            .addItem("Play around")
                            .addItem("Do something")
                            .build())
                    .moreInformation(
                        DashboardMoreInformationDto.builder()
                            .title("How to stay safe")
                            .heading(
                                "Washing your hands is the best way to help you stay safe. Here's how to do it")
                            .addInstruction(
                                "Wet your hands with clean, running water. Turn off the tap and apply soap.")
                            .addInstruction(
                                "Lather your hands by rubbing them together. Get the backs of your hands, between your fingers, and under your nails.")
                            .build())
                    .build())
            .detailedData(
                DetailedDataDto.builder()
                    .addDataInsight(
                        DataInsightsDto.builder().title("Total cases").value("20").build())
                    .addDataInsight(
                        DataInsightsDto.builder().title("Total Deaths").value("20").build())
                    .addTimelinePhase(
                        DataTimelineDto.builder()
                            .date("5th")
                            .day("Sat")
                            .title("540 total cases")
                            .subtitle("21 total deaths")
                            .build())
                    .addTimelinePhase(
                        DataTimelineDto.builder()
                            .date("4th")
                            .day("Fri")
                            .title("210 total cases")
                            .subtitle("20 total deaths")
                            .build())
                    .build())
            .build();
  }
}
