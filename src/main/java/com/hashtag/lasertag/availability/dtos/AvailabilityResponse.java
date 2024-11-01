package com.hashtag.lasertag.availability.dtos;

import com.hashtag.lasertag.activity.Activity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailabilityResponse {

  Activity activity;
  Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>> availability;

}
