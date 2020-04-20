package com.easycompany.trappd.model.dto;

import com.easycompany.trappd.model.dto.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class QuickGameDto {
  private String category;
  private String title;
  private String detail;
  private boolean enabled;
  private String source;
  private String externalLink;
}
