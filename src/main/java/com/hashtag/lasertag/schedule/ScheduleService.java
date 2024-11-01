package com.hashtag.lasertag.schedule;

import static com.hashtag.lasertag.shared.exceptions.ErrorMessages.SCHEDULE_NOT_FOUND;

import com.hashtag.lasertag.schedule.dtos.SchedulePatchRequest;
import com.hashtag.lasertag.schedule.dtos.ScheduleCreateUpdateRequest;
import com.hashtag.lasertag.shared.exceptions.BadRequestException;
import com.hashtag.lasertag.shared.exceptions.ConflictException;
import com.hashtag.lasertag.shared.exceptions.ErrorMessages;
import com.hashtag.lasertag.shared.exceptions.InvalidOperationException;
import com.hashtag.lasertag.shared.exceptions.ResourceNotFoundException;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleService {

  final ScheduleRepository scheduleRepository;

  /**
   * Get active schedule by date and if not, get the default (base) schedule
   *
   * @param date date for filtration
   * @return current schedule that applies for a specific date
   */
  public Schedule findCurrentSchedule(LocalDate date) {
    return scheduleRepository.findActiveNonMainScheduleByDate(date)
        .orElseGet(() -> scheduleRepository.findMainSchedule().orElseThrow(
            () -> new ResourceNotFoundException(Schedule.class, date, SCHEDULE_NOT_FOUND)
        ));
  }

  /**
   * Find all non-deleted schedules
   *
   * @return list of all non-deleted schedules
   */
  public List<Schedule> findNonDeletedSchedules() {
    return scheduleRepository.findAllNonDeleted();
  }

  /**
   * Create a new schedule
   *
   * @param scheduleCreateUpdateRequest schedule data to create
   * @return created schedule, including the generated ID
   */
  public Schedule createSchedule(ScheduleCreateUpdateRequest scheduleCreateUpdateRequest) {
    validateSchedule(scheduleCreateUpdateRequest);
    return scheduleRepository.save(scheduleCreateUpdateRequest.toSchedule());
  }

  /**
   * Update an existing schedule
   *
   * @param scheduleCreateUpdateRequest schedule data to update
   * @return updated schedule
   */
  @Transactional
  public Schedule updateSchedule(ScheduleCreateUpdateRequest scheduleCreateUpdateRequest) {
    validateSchedule(scheduleCreateUpdateRequest);

    Schedule schedule = scheduleRepository
        .findByIdAndDeletedIsFalse(scheduleCreateUpdateRequest.getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            Schedule.class, scheduleCreateUpdateRequest.getId(), SCHEDULE_NOT_FOUND
        ));

    schedule.setName(StringUtils.trim(scheduleCreateUpdateRequest.getName()));
    schedule.setDescription(StringUtils.trim(scheduleCreateUpdateRequest.getDescription()));
    schedule.setStartDate(scheduleCreateUpdateRequest.getStartDate());
    schedule.setEndDate(scheduleCreateUpdateRequest.getEndDate());
    schedule.setMonday(scheduleCreateUpdateRequest.getMonday());
    schedule.setTuesday(scheduleCreateUpdateRequest.getTuesday());
    schedule.setWednesday(scheduleCreateUpdateRequest.getWednesday());
    schedule.setThursday(scheduleCreateUpdateRequest.getThursday());
    schedule.setFriday(scheduleCreateUpdateRequest.getFriday());
    schedule.setSaturday(scheduleCreateUpdateRequest.getSaturday());
    schedule.setSunday(scheduleCreateUpdateRequest.getSunday());
    return schedule;
  }

  /**
   * Patch active value for schedule
   *
   * @param scheduleId to identify schedule to update activation
   */
  @Transactional
  public void patchSchedule(Long scheduleId, SchedulePatchRequest schedulePatchRequest) {
    Schedule schedule = scheduleRepository.findByIdAndDeletedIsFalse(scheduleId)
        .orElseThrow(() -> new ResourceNotFoundException(
            Schedule.class, scheduleId, ErrorMessages.SCHEDULE_NOT_FOUND
        ));

    if (schedulePatchRequest.getMain() != null && schedulePatchRequest.getMain()) {
      setMainSchedule(schedule);
    }

    if (schedulePatchRequest.getActive() != null) {
      if (schedule.isMain() && !schedulePatchRequest.getActive()) {
        throw new InvalidOperationException(ErrorMessages.CANT_DEACTIVATE_MAIN_SCHEDULE);
      }

      schedule.setActive(schedulePatchRequest.getActive());
    }
  }

  /**
   * Update the default (base) schedule, by setting all schedules to false. When base is updated,
   * schedule gets activated.
   *
   * @param schedule to make default
   */
  private void setMainSchedule(Schedule schedule) {
    // Remove base = true from all other schedules
    scheduleRepository.resetMainSchedule();

    schedule.setMain(true);
    schedule.setActive(true);
  }

  /**
   * Safe delete schedule by ID
   *
   * @param scheduleId to identify schedule to delete
   */
  @Transactional
  public void safeDeleteSchedule(Long scheduleId) {
    // validation that no slots are running in this schedule
    Schedule schedule = scheduleRepository.findByIdAndDeletedIsFalse(scheduleId)
        .orElseThrow(() -> new ResourceNotFoundException(
            Schedule.class, scheduleId, SCHEDULE_NOT_FOUND
        ));

    if (schedule.isMain()) {
      throw new InvalidOperationException(ErrorMessages.CANT_DELETE_MAIN_SCHEDULE);
    }

    schedule.setDeleted(true);
  }

  public void validateTimeRangeRespectsSchedule(Schedule schedule, LocalDate date,
      LocalTime startTime) {

    for (var timeRange : schedule.getTimeRangesByDay(date.getDayOfWeek())) {
      // If time is respecting schedule, let it be
      if (timeRange.isLocalTimeBetween(startTime)) {
        return;
      }
    }

    throw new InvalidOperationException(ErrorMessages.TIME_PROVIDED_NOT_IN_SCHEDULE);
  }

  /**
   * Validates schedule data for creation / update
   *
   * @param scheduleCreateUpdateRequest data to validate
   */
  private void validateSchedule(ScheduleCreateUpdateRequest scheduleCreateUpdateRequest) {
    List<Schedule> schedulesInSamePeriod = scheduleRepository.findSchedulesInSamePeriod(
        scheduleCreateUpdateRequest.getId(), scheduleCreateUpdateRequest.getStartDate(),
        scheduleCreateUpdateRequest.getEndDate()
    );

    if (!schedulesInSamePeriod.isEmpty()) {
      throw new ConflictException(ErrorMessages.SCHEDULE_PERIOD_CONFLICT);
    }

    if (!scheduleCreateUpdateRequest.getStartDate()
        .isBefore(scheduleCreateUpdateRequest.getEndDate())) {
      throw new BadRequestException(ErrorMessages.START_DATE_BEFORE_END_DATE);
    }

  }

}