package com.easycompany.trappd.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.easycompany.trappd.model.dto.response.BaseResponse;
import com.easycompany.trappd.service.EngineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class EngineControllerTest {

  @MockBean EngineService engineService;

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;
  /*
  @Test
  void uploadDocument_provideValidRequest_expectValidResponse() throws Exception {
    // Given
    BaseResponse uploadDocument =
        BaseResponse.builder()
            .message("Upload request successful")
            .status(HttpStatus.OK.getReasonPhrase())
            .build();
    MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
    request.addFile(
        new MockMultipartFile("data", "filename", "application/octet-stream", "Prince".getBytes()));

    // When
    Mockito.doReturn(uploadDocument).when(engineService).uploadDataToS3(request);
    // Then
    mockMvc
        .perform(post("/engine/updateData").postProcessRequest(request))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(is(objectMapper.writeValueAsString(uploadDocument))));
  }*/
}
