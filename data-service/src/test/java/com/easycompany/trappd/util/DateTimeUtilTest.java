package com.easycompany.trappd.util;

import com.amazonaws.services.opsworks.model.App;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DateTimeUtilTest {
  @Test
  public void parseLocalDate_giveDateStringWithFormat_expectValidResult() {
    // Given
    String dateString = "13/04/2020";
    String format = AppConstants.ANNOUNCED_DATE_FORMAT;
    String parsed;
    // When
    LocalDate localDate = DateTimeUtil.parseLocalDate(dateString, format);

    // Then
    Assertions.assertEquals(13, localDate.getDayOfMonth());
  }
}
