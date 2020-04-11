package com.easycompany.trappd.service;

import com.easycompany.trappd.model.dto.response.BaseResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class EngineService {

  public BaseResponse uploadDataToS3(MultipartFile multipartFile) throws IOException {
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
    String contents = bufferedReader.lines().collect(Collectors.joining("\n"));

    log.debug(contents.substring(0, contents.length() > 1000 ? 1000 : contents.length()));
    return BaseResponse.builder()
        .message("Upload request successful")
        .status(HttpStatus.OK.getReasonPhrase())
        .build();
  }
}
