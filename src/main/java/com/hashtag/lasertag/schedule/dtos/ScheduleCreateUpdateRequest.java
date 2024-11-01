package com.hashtag.lasertag.schedule.dtos;

import com.hashtag.lasertag.schedule.Schedule;
import com.hashtag.lasertag.shared.TimeRange;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO used for creation / update of Schedule
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ScheduleCreateUpdateRequest {

    Long id;

    @NotNull(message = "Name cannot be null")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description;

    List<TimeRange> monday;
    List<TimeRange> tuesday;
    List<TimeRange> wednesday;
    List<TimeRange> thursday;
    List<TimeRange> friday;
    List<TimeRange> saturday;
    List<TimeRange> sunday;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent(message = "End date must be today or in the future")
    LocalDate endDate;

    public Schedule toSchedule() {
        Schedule schedule = new Schedule();
        schedule.setId(this.id);
        schedule.setName(StringUtils.trim(this.name));
        schedule.setDescription(StringUtils.trim(this.description));
        schedule.setMonday(this.monday);
        schedule.setTuesday(this.tuesday);
        schedule.setWednesday(this.wednesday);
        schedule.setThursday(this.thursday);
        schedule.setFriday(this.friday);
        schedule.setSaturday(this.saturday);
        schedule.setSunday(this.sunday);
        schedule.setStartDate(this.startDate);
        schedule.setEndDate(this.endDate);
        schedule.setMain(false);
        schedule.setActive(false);
        schedule.setDeleted(false);
        return schedule;
    }
}