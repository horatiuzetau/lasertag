package com.hashtag.lasertag.slot.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlotDto {

  @NotNull(message = "Data should be provided")
  LocalDate date;

  @NotNull(message = "Time range is required")
  LocalTime startTime;

  @Positive
  int bookedSpots;

  List<SlotDto> bundledSlots;

  @NotNull
  Long activityId;

}