package com.easycompany.trappd.model.entity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "quick_game")
@Cacheable
public class QuickGameEntity extends AbstractBaseEntity {

  private String category;
  private String title;
  private String detail;
  private boolean enabled;
  private String source;
  private String externalLink;
}

