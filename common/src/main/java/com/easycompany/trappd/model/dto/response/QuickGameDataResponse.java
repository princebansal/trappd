package com.easycompany.trappd.model.dto.response;

import com.easycompany.trappd.model.dto.QuickGameDto;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class QuickGameDataResponse extends BaseResponse {
  List<QuickGameDto> quickGameList;
}
