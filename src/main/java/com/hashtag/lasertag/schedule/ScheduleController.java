package com.hashtag.lasertag.schedule;

import com.hashtag.lasertag.schedule.dtos.SchedulePatchRequest;
import com.hashtag.lasertag.schedule.dtos.ScheduleCreateUpdateRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleController {

    final ScheduleService scheduleService;

    /**
     * Get the current schedule for a specific date.
     *
     * @param date the date for which to get the schedule
     * @return ResponseEntity with the current schedule
     */
    @GetMapping("/{date}")
    public ResponseEntity<Schedule> getCurrentSchedule(@PathVariable LocalDate date) {
        Schedule currentSchedule = scheduleService.findCurrentSchedule(date);
        return ResponseEntity.ok(currentSchedule);
    }

    /**
     * Get all non-deleted schedules.
     *
     * @return ResponseEntity with the list of all non-deleted schedules
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.findNonDeletedSchedules();
        return ResponseEntity.ok(schedules);
    }

    /**
     * Create a new schedule.
     *
     * @param scheduleCreateUpdateRequest the data for the new schedule
     * @return ResponseEntity with the created schedule
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody ScheduleCreateUpdateRequest scheduleCreateUpdateRequest) {
        Schedule createdSchedule = scheduleService.createSchedule(scheduleCreateUpdateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    /**
     * Update an existing schedule.
     *
     * @param scheduleCreateUpdateRequest the data to update
     * @return ResponseEntity with the updated schedule
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<Schedule> updateSchedule(@Valid @RequestBody ScheduleCreateUpdateRequest scheduleCreateUpdateRequest) {
        Schedule updatedSchedule = scheduleService.updateSchedule(scheduleCreateUpdateRequest);
        return ResponseEntity.ok(updatedSchedule);
    }

    /**
     * Patch the active status of a schedule.
     *
     * @param scheduleId       the ID of the schedule to update
     * @param schedulePatchRequest the patch object
     * @return ResponseEntity with no content
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<Void> patchSchedule(@PathVariable Long scheduleId,
                                              @Valid @RequestBody SchedulePatchRequest schedulePatchRequest) {
        scheduleService.patchSchedule(scheduleId, schedulePatchRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Safe delete a schedule by ID.
     *
     * @param scheduleId the ID of the schedule to delete
     * @return ResponseEntity with no content
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> safeDeleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.safeDeleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}