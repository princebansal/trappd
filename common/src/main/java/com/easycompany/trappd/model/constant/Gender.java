package com.easycompany.trappd.model.constant;

public enum Gender {
  M("Male"),
  F("Female"),
  O("Others");

  private final String label;

  Gender(String label) {
    this.label = label;
  }
}
