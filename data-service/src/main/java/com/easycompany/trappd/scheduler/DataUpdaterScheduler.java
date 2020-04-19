package com.easycompany.trappd.scheduler;

import com.easycompany.trappd.service.DataUpdaterServiceV2;
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
@Order(4)
public class DataUpdaterScheduler {

  private final DataUpdaterServiceV2 dataUpdaterServiceV2;

  @Autowired
  public DataUpdaterScheduler(DataUpdaterServiceV2 dataUpdaterServiceV2) {
    this.dataUpdaterServiceV2 = dataUpdaterServiceV2;
  }

  @SneakyThrows
  @Scheduled(fixedDelay = 50 * 60 * 1000)
  private void update() {
    dataUpdaterServiceV2.update();
  }
}
