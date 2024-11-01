package com.hashtag.lasertag.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeRange {

  @Column(name = "start_time", nullable = false)
  LocalTime startTime;
  @Column(name = "end_time", nullable = false)
  LocalTime endTime;

  public boolean isLocalTimeBetween(LocalTime time) {
    return (this.startTime.equals(time) || this.startTime.isBefore(time))
        && (this.endTime.equals(time) || this.endTime.isAfter(time));
  }
}