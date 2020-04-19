package com.easycompany.trappd.cache;

import com.easycompany.trappd.model.entity.CityEntity;
import com.easycompany.trappd.repository.CityRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class CityCache {
  private static Map<String, CityEntity> cityStateCodeToCityEntityMap = new HashMap<>();

  private final CityRepository cityRepository;

  @Autowired
  public CityCache(CityRepository cityRepository) {
    this.cityRepository = cityRepository;
  }

  @PostConstruct
  public void init() {

    List<CityEntity> cityEntityList = cityRepository.findAll();
    cityStateCodeToCityEntityMap =
        cityEntityList.stream()
            .collect(
                Collectors.toMap(
                    cityEntity ->
                        (cityEntity.getCode() + cityEntity.getState().getCode()).toLowerCase(),
                    cityEntity -> cityEntity));
  }

  public Optional<CityEntity> getCityEntity(String code) {
    if (cityStateCodeToCityEntityMap.containsKey(code.toLowerCase())) {
      return Optional.ofNullable(cityStateCodeToCityEntityMap.get(code.toLowerCase()));
    } else {
      return Optional.empty();
    }
  }

  public CityEntity getCityEntity(String code1, String code2) {
    return getCityEntity(code1).orElseGet(() -> getCityEntity(code2).orElse(null));
  }
}
