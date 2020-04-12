package com.easycompany.trappd.controller;

import com.easycompany.trappd.model.dto.response.BaseResponse;
import com.easycompany.trappd.service.EngineService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/engine")
public class EngineController {

  private final EngineService engineService;

  @Autowired
  public EngineController(EngineService engineService) {
    this.engineService = engineService;
  }

  @PostMapping(value = "updateData")
  @ResponseBody
  public ResponseEntity<BaseResponse> updateData(MultipartHttpServletRequest data)
      throws IOException {

    return ResponseEntity.ok(engineService.updateDataInEngine(data.getFile("data")));
  }
}
