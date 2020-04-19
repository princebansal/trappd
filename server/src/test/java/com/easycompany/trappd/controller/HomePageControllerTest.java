package com.easycompany.trappd.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.easycompany.trappd.model.dto.CityDto;
import com.easycompany.trappd.model.dto.CountryDto;
import com.easycompany.trappd.model.dto.DashboardDto;
import com.easycompany.trappd.model.dto.DashboardMoreInformationDto;
import com.easycompany.trappd.model.dto.DataInsightsCardDto;
import com.easycompany.trappd.model.dto.DataInsightsDto;
import com.easycompany.trappd.model.dto.DataTimelineDto;
import com.easycompany.trappd.model.dto.DetailedDataDto;
import com.easycompany.trappd.model.dto.StateDto;
import com.easycompany.trappd.model.dto.ThingsToDoCardDto;
import com.easycompany.trappd.model.dto.response.GetAllCitiesResponse;
import com.easycompany.trappd.model.dto.response.GetHomePageDataResponse;
import com.easycompany.trappd.service.HomePageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class HomePageControllerTest {

  @MockBean HomePageService homePageService;

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Test
  void getAllCities_provideValidRequest_expectValidResponse() throws Exception {
    // Given
    CountryDto requestCountry = CountryDto.builder().name("India").code("IN").build();
    GetAllCitiesResponse getAllCitiesResponse =
        GetAllCitiesResponse.builder()
            .countryDto(requestCountry)
            .addCity(CityDto.builder().code("BLR").name("Bangalore").build())
            .addCity(CityDto.builder().code("BOM").name("Mumbai").build())
            .addCity(CityDto.builder().code("CCU").name("Kolkata").build())
            .addCity(CityDto.builder().code("DEL").name("Delhi").build())
            .build();
    // When
    Mockito.doReturn(getAllCitiesResponse)
        .when(homePageService)
        .getListOfAllCitiesForCountry(requestCountry.getCode());
    // Then
    mockMvc
        .perform(
            get("/home/getAllCities")
                .param("countryCode", requestCountry.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(is(objectMapper.writeValueAsString(getAllCitiesResponse))));
  }

  @Test
  void getHomePageData_provideValidRequest_expectValidResponse() throws Exception {
    // Given
    CountryDto requestCountry = CountryDto.builder().name("India").code("IN").build();
    CityDto requestCity = CityDto.builder().code("BLR").name("Bangalore").build();
    GetHomePageDataResponse getHomePageDataResponse =
        GetHomePageDataResponse.builder()
            .city(requestCity)
            .country(requestCountry)
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
    // When
    Mockito.doReturn(getHomePageDataResponse)
        .when(homePageService)
        .getHomePageDataForCountryAndCity(requestCountry.getCode(),
            requestCity.getCode());
    // Then
    mockMvc
        .perform(
            get("/home/getHomePageData")
                .param("countryCode", requestCountry.getCode())
                .param("cityCode", requestCity.getCode())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(is(objectMapper.writeValueAsString(getHomePageDataResponse))));
  }
}
