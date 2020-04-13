package com.easycompany.trappd.scheduler;

import com.easycompany.trappd.service.DataUpdaterService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
@Order(2)
public class DataUpdaterScheduler {

  private final DataUpdaterService dataUpdaterService;

  @Autowired
  public DataUpdaterScheduler(DataUpdaterService dataUpdaterService) {
    this.dataUpdaterService = dataUpdaterService;
  }

  @SneakyThrows
  @Scheduled(fixedDelay = 50 * 60 * 1000)
  private void update() {
    dataUpdaterService.update();
  }
}
