package com.easycompany.trappd.model.constant;

import com.easycompany.trappd.exception.BadRequestException;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The enum Geography type.
 */
public enum GeographyType {
  /**
   * The City.
   */
  CITY("city"),
  /**
   * The State.
   */
  STATE("state"),
  /**
   * The Country.
   */
  COUNTRY("country");

  /**
   * The constant enumMap.
   */
  public static Map<String, GeographyType> enumMap = new ConcurrentHashMap<>();

  static {
    Arrays.stream(GeographyType.values()).forEach(entry -> enumMap.put(entry.geographyName, entry));
  }

  /**
   * The Geography name.
   */
  private String geographyName;

  /**
   * Instantiates a new Geography type.
   *
   * @param geographyName the geography name
   */
  GeographyType(String geographyName) {
    this.geographyName = geographyName;
  }

  /**
   * Gets enum by name.
   *
   * @param geographyName the geography name
   * @return the enum by name
   * @throws BadRequestException the bad request exception
   */
  public static GeographyType getEnumByName(String geographyName) throws BadRequestException {
    if (!enumMap.containsKey(geographyName)) {
      throw new BadRequestException("No such geography type " + geographyName);
    }
    return enumMap.get(geographyName);
  }

  public static Map<String, GeographyType> getEnumMap() {
    return enumMap;
  }

  /**
   * Gets geography name.
   *
   * @return the geography name
   */
  public String getGeographyName() {
    return geographyName;
  }
}
