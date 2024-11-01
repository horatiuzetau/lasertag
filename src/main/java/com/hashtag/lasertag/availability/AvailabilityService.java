package com.hashtag.lasertag.availability;

import com.hashtag.lasertag.activity.Activity;
import com.hashtag.lasertag.activity.ActivityInBundle;
import com.hashtag.lasertag.activity.ActivityService;
import com.hashtag.lasertag.activity.enums.ActivityType;
import com.hashtag.lasertag.availability.dtos.AvailabilitySlotSummary;
import com.hashtag.lasertag.schedule.ScheduleService;
import com.hashtag.lasertag.shared.TimeRange;
import com.hashtag.lasertag.slot.Slot;
import com.hashtag.lasertag.slot.SlotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailabilityService {

  final SlotRepository slotRepository;
  final ScheduleService scheduleService;
  final ActivityService activityService;

  public Object getAvailability(LocalDate startDate) {

    LocalDate endDate = startDate.plusDays(1);

    Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> bookedSlotsMap =
        computeBookedSlotsMap(startDate, endDate);

    Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> availabilityMap =
        computeAvailabilityMap(bookedSlotsMap, startDate, endDate);

    filterAvailabilityForBundles(availabilityMap);

    return availabilityMap.entrySet().stream()
        .collect(Collectors.toMap(
            o -> o.getKey().getId(),
            Entry::getValue
        ));
  }

  /**
   * Based on the slots booked during the date period given, compute a strategic map that holds
   * capacity and bookedSpots for each time slot of the day for each activity
   *
   * @param startDate starting date for period of booked slots
   * @param endDate   ending date for period of booked slots
   * @return
   */
  private Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> computeBookedSlotsMap(
      LocalDate startDate, LocalDate endDate) {

    return slotRepository
        .findAllSlotsByStartDateAndEndDate(startDate, endDate)
        .stream()
        .collect(Collectors.groupingBy(
            Slot::getActivity,
            Collectors.groupingBy(
                Slot::getDate,
                Collectors.groupingBy(
                    Slot::getStartTime,
                    Collectors.reducing(
                        new AvailabilitySlotSummary(0, 0),
                        slot -> new AvailabilitySlotSummary(slot.getActivity().getCapacity(),
                            slot.getBookedSpots()),
                        (summary1, summary2) -> new AvailabilitySlotSummary(
                            summary1.getCapacity() != 0
                                ? summary1.getCapacity() : summary2.getCapacity(),
                            summary1.getBookedSpots() + summary2.getBookedSpots()
                        )
                    )
                )
            )
        ));
  }

  private Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> computeAvailabilityMap(
      Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> bookedSlotsMap,
      LocalDate startDate, LocalDate endDate) {

    // Get Schedule for time ranges
    var schedule = scheduleService.findCurrentSchedule(LocalDate.now());
    // Get all active Activities to compute available time frames
    var activities = activityService.findAllActiveActivities();

    Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> availabilityMap = new HashMap<>();

    // Iterate through each activity
    for (Activity activity : activities) {
      Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>> dateTimeMap = new TreeMap<>();

      // Iterate through all the dates from the range
      for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
        // Get the time ranges for the day from the schedule
        List<TimeRange> timeRanges = schedule.getTimeRangesByDay(date.getDayOfWeek());

        Map<LocalTime, AvailabilitySlotSummary> availableTimes = new TreeMap<>();

        // Iterate through each time range
        for (TimeRange timeRange : timeRanges) {

          LocalDateTime endLocalDateTime = LocalDateTime.of(
              timeRange.getEndTime().equals(LocalTime.of(0, 0))
                  ? date.plusDays(1) : date,
              timeRange.getEndTime()
          );

          // For loop to create time slots until endTime is reached or passed
          for (
              LocalDateTime localDateTime = LocalDateTime.of(
                  date, timeRange.getStartTime()
              );
              isRoomForTimeFrame(
                  localDateTime, endLocalDateTime, activity.getDuration(),
                  activity.getRecoveryTime()
              );
              localDateTime = localDateTime.plusMinutes(
                  activity.getDuration() + activity.getRecoveryTime())
          ) {
            LocalTime time = localDateTime.toLocalTime();
            // Get SlotSummary from the booked slots
            AvailabilitySlotSummary availabilitySlotSummary = AvailabilitySlotSummary
                .getSlotSummaryFromBookedSlotsMap(
                    bookedSlotsMap, date, localDateTime.toLocalTime(), activity
                );

            // If Slot is full, don't add it as available
            if (!availabilitySlotSummary.isFull()) {
              availableTimes.put(time, availabilitySlotSummary);
            }
          }
        }

        if (!availableTimes.isEmpty()) {
          dateTimeMap.put(date, availableTimes);
        }
      }

      if (!dateTimeMap.isEmpty()) {
        availabilityMap.put(activity, dateTimeMap);
      }
    }

    return availabilityMap;
  }

  private boolean isRoomForTimeFrame(LocalDateTime localDateTime,
      LocalDateTime endLocalDateTime, int duration, int recoveryTime) {

    return localDateTime.plusMinutes(duration + recoveryTime).isBefore(endLocalDateTime)
        || localDateTime.plusMinutes(duration + recoveryTime).equals(endLocalDateTime);
  }

  private void filterAvailabilityForBundles(
      Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> availabilityMap) {

    // Iterate over each entry in availabilityMap for activities of type BUNDLE
    for (Map.Entry<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> entry : availabilityMap.entrySet()) {
      Activity bundleActivity = entry.getKey();

      // Skip non-BUNDLE activities
      if (bundleActivity.getType() != ActivityType.BUNDLE) {
        continue;
      }

      // Iterate through each date
      Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>> bundleDateMap = entry.getValue();
      for (Map.Entry<LocalDate, Map<LocalTime, AvailabilitySlotSummary>> dateEntry : bundleDateMap.entrySet()) {
        LocalDate date = dateEntry.getKey();

        // Collect times to remove (if bundled activities don't have enough availability)
        List<LocalTime> timesToRemove = new ArrayList<>();

        // Iterate through each time slot
        Map<LocalTime, AvailabilitySlotSummary> bundleTimeMap = dateEntry.getValue();
        for (LocalTime bundleStartTime : bundleTimeMap.keySet()) {
          // Check availability for all bundled activities at that time + bundle duration
          for (ActivityInBundle activityInBundle : bundleActivity.getBundledActivities()) {
            var isActivityInBundleFullyAvailable = isActivityInBundleFullyAvailable(
                availabilityMap, bundleStartTime, activityInBundle, bundleActivity, date
            );

            if (!isActivityInBundleFullyAvailable) {
              timesToRemove.add(bundleStartTime);
            }
          }
        }

        // Remove the unavailable times from the map
        timesToRemove.forEach(bundleTimeMap::remove);
      }
    }
  }

  private static boolean isActivityInBundleFullyAvailable(
      Map<Activity, Map<LocalDate, Map<LocalTime, AvailabilitySlotSummary>>> availabilityMap,
      LocalTime bundleTime, ActivityInBundle activityInBundle, Activity bundleActivity,
      LocalDate date) {

    var bundleEndTime = bundleTime.plusMinutes(bundleActivity.getDuration());
    var availableTimesForActivity = availabilityMap.get(activityInBundle.getActivity())
        .getOrDefault(date, new HashMap<>())
        .keySet()
        .stream()
        .filter(activityTime ->
            (activityTime.equals(bundleTime) || activityTime.isAfter(bundleTime))
                && (activityTime.equals(bundleEndTime) || activityTime.isBefore(bundleEndTime)))
        .count();
    return availableTimesForActivity > activityInBundle.getSize();
  }


}
