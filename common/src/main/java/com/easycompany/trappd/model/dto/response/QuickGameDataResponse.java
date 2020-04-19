package com.easycompany.trappd.model.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class QuickGameDataResponse extends BaseResponse {
  private String category;
  private String title;
  private String detail;
  private boolean enabled;
  private String source;
  private String externalLink;
}
