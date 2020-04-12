package com.easycompany.trappd.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class DateTimeUtil {
  public static LocalDateTime currentTimeInUTC() {
    return LocalDateTime.now(ZoneId.of("UTC"));
  }
}
