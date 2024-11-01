package com.hashtag.lasertag.availability.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hashtag.lasertag.activity.Activity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AvailabilitySlotSummary {

  int capacity;
  int bookedSpots;

  public boolean isFull() {
    return getAvailableSpots() <= 0;
  }

  public int getAvailableSpots() {
    return this.capacity - this.bookedSpots;
  }

  @JsonIgnore
  public static AvailabilitySlotSummary getSlotSummaryFromBookedSlotsMap(
      Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> bookedSlotsMap,
      LocalDate date, LocalTime time, Activity activity) {

    return Optional.ofNullable(bookedSlotsMap.get(activity)) // Get activity
        .map(o -> o.get(date)) // Get list of times
        .map(o -> o.get(time)) // Get SlotSummary
        .orElseGet(() -> new AvailabilitySlotSummary(activity.getCapacity(),
            0)); // Get empty SlotSummary if no SlotSummary was found
  }

}