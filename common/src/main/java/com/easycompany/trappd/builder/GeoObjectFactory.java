package com.easycompany.trappd.builder;

import com.easycompany.trappd.model.constant.GeographyType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeoObjectFactory {

  private final CityDashboardObjectBuilder cityDashboardObjectBuilder;
  private final StateDashboardObjectBuilder stateDashboardObjectBuilder;
  private final CountryDashboardObjectBuilder countryDashboardObjectBuilder;

  @Autowired
  public GeoObjectFactory(CityDashboardObjectBuilder cityDashboardObjectBuilder,
                          StateDashboardObjectBuilder stateDashboardObjectBuilder,
                          CountryDashboardObjectBuilder countryDashboardObjectBuilder) {
    this.cityDashboardObjectBuilder = cityDashboardObjectBuilder;
    this.stateDashboardObjectBuilder = stateDashboardObjectBuilder;
    this.countryDashboardObjectBuilder = countryDashboardObjectBuilder;
  }

  public BaseDashboardObjectBuilder getDashboardObjectBuilder(GeographyType geoType) {
    switch (geoType) {
      case CITY:
        return cityDashboardObjectBuilder;

      case STATE:
        return stateDashboardObjectBuilder;

      case COUNTRY:
        return countryDashboardObjectBuilder;

      default:
        return null;
    }
  }


}
