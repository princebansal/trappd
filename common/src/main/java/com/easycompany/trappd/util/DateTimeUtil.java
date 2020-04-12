package com.easycompany.trappd.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {
  public static LocalDateTime currentTimeInUTC() {
    return LocalDateTime.now(ZoneId.of("UTC"));
  }

  public static LocalDate todaysDateInUTC() {
    return LocalDate.now(ZoneId.of("UTC"));
  }

  public static String todaysDateInUTCFormatted(String format) {
    return LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern(format));
  }

  public static LocalDate parseLocalDate(String dateString, String format) {
    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
  }

  public static String formatLocalDate(LocalDate localDate, String format) {
    return localDate.format(DateTimeFormatter.ofPattern(format));
  }
}
