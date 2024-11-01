package com.hashtag.lasertag.slot.dtos;

import com.hashtag.lasertag.client.dtos.ClientCreateUpdateRequest;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlotBatchCreateRequest {

  List<SlotDto> slots;

  @NotNull(message = "Client information should be provided")
  ClientCreateUpdateRequest client;

}
