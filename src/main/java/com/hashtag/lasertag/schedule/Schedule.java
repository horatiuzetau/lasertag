package com.hashtag.lasertag.schedule;


import com.hashtag.lasertag.schedule.converters.TimeRangeConverter;
import com.hashtag.lasertag.shared.TimeRange;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "schedules")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  Instant createdAt = Instant.now();

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt = Instant.now();

  @Column(nullable = false, length = 100)
  String name;

  @Column(length = 1000)
  String description;

  @Column(nullable = false)
  boolean main = false;

  @Column(nullable = false)
  boolean active = false;

  @Column(nullable = false)
  boolean deleted = false;

  @Column(name = "monday")
  @Convert(converter = TimeRangeConverter.class)
  @ColumnTransformer(write = "?::time_range[]")
  List<TimeRange> monday;

  @Column(name = "tuesday")
  @Convert(converter = TimeRangeConverter.class)
  @ColumnTransformer(write = "?::time_range[]")
  List<TimeRange> tuesday;

  @Column(name = "wednesday")
  @Convert(converter = TimeRangeConverter.class)
  @ColumnTransformer(write = "?::time_range[]")
  List<TimeRange> wednesday;

  @Column(name = "thursday")
  @Convert(converter = TimeRangeConverter.class)
  @ColumnTransformer(write = "?::time_range[]")
  List<TimeRange> thursday;

  @Column(name = "friday")
  @Convert(converter = TimeRangeConverter.class)
  @ColumnTransformer(write = "?::time_range[]")
  List<TimeRange> friday;

  @Column(name = "saturday")
  @Convert(converter = TimeRangeConverter.class)
  @ColumnTransformer(write = "?::time_range[]")
  List<TimeRange> saturday;

  @Column(name = "sunday")
  @Convert(converter = TimeRangeConverter.class)
  @ColumnTransformer(write = "?::time_range[]")
  List<TimeRange> sunday;

  @Column(name = "start_date", nullable = false)
  LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  LocalDate endDate;

  public List<TimeRange> getTimeRangesByDay(DayOfWeek dayOfWeek) {
    return switch (dayOfWeek) {
      case MONDAY -> monday;
      case TUESDAY -> tuesday;
      case WEDNESDAY -> wednesday;
      case THURSDAY -> thursday;
      case FRIDAY -> friday;
      case SATURDAY -> saturday;
      case SUNDAY -> sunday;
    };
  }
}
