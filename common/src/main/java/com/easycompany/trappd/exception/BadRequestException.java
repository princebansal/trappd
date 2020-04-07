package com.easycompany.trappd.exception;

import lombok.EqualsAndHashCode.Exclude;

public class BadRequestException extends Exception {

  public BadRequestException(String s) {
    super(s);
  }
}
