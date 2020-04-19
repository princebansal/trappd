package com.easycompany.trappd.helper;

import com.easycompany.trappd.model.entity.AbstractBaseEntity;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DataUpdaterHelper<T, E extends AbstractBaseEntity> {

  private Function callback = null;

  abstract void update(List<T> latestCaseList);

  abstract void checkAndUpdate(List<T> latestCaseList, List<E> listOfExistingRecords);

  abstract void bulkUpdate(List<E> recordsToUpdate);

  abstract void bulkInsert(List<T> recordsToInsert);

  abstract boolean shouldUpdate(T dto, E entity);

  void onUpdateSuccess(String message) {
    log.info(message);
    if (callback != null) {
      callback.apply(null);
    }
  }

  void onUpdateError(Exception e) {
    log.error("Error occured while updating {}", e);
  }

  void setCallback(Function callbackFunction) {
    this.callback = callbackFunction;
  }
}
