package com.easycompany.trappd.cache;

import com.easycompany.trappd.model.entity.StateEntity;
import com.easycompany.trappd.repository.StateRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public final class StateCache {
  private static Map<String, StateEntity> stateCodeToStateEntityMap = new HashMap<>();

  private final StateRepository stateRepository;

  @Autowired
  public StateCache(StateRepository stateRepository) {
    this.stateRepository = stateRepository;
  }

  @PostConstruct
  public void init() {
    List<StateEntity> stateEntityList = stateRepository.findAll();
    stateCodeToStateEntityMap =
        stateEntityList.stream()
            .collect(
                Collectors.toMap(
                    stateEntity -> stateEntity.getCode().toLowerCase(),
                    stateEntity -> stateEntity));
  }

  public Optional<StateEntity> getStateEntity(String code) {
    if (stateCodeToStateEntityMap.containsKey(code.toLowerCase())) {
      return Optional.ofNullable(stateCodeToStateEntityMap.get(code.toLowerCase()));
    } else {
      return Optional.empty();
    }
  }

  public StateEntity getStateEntity(String code1, String code2) {
    return getStateEntity(code1).orElseGet(() -> getStateEntity(code2).orElse(null));
  }
}
