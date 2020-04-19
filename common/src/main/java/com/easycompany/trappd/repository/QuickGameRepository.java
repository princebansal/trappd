package com.easycompany.trappd.repository;

import com.easycompany.trappd.model.entity.QuickGameEntity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuickGameRepository extends JpaRepository<QuickGameEntity, Long> {

  List<QuickGameEntity> findByEnabledTrue();
}
