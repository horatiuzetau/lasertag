package com.hashtag.lasertag.slot;

import com.hashtag.lasertag.slot.dtos.AdminSlotResponseDto;
import com.hashtag.lasertag.slot.dtos.SlotBatchCreateRequest;
import com.hashtag.lasertag.slot.dtos.SlotCreateRequest;
import com.hashtag.lasertag.slot.dtos.SlotDto;
import com.hashtag.lasertag.slot.dtos.SlotPatchRequest;
import com.hashtag.lasertag.slot.enums.SlotStatus;
import com.hashtag.lasertag.shared.exceptions.BadRequestException;
import com.hashtag.lasertag.shared.exceptions.ErrorMessages;
import com.hashtag.lasertag.shared.exceptions.InvalidOperationException;
import com.hashtag.lasertag.slot.exceptions.UnavailableSpotException;
import com.hashtag.lasertag.activity.Activity;
import com.hashtag.lasertag.client.Client;
import com.hashtag.lasertag.schedule.Schedule;
import com.hashtag.lasertag.activity.ActivityService;
import com.hashtag.lasertag.client.ClientService;
import com.hashtag.lasertag.schedule.ScheduleService;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotService {

  final String LOG_PREFIX = "SLOT SERVICE";

  final SlotRepository slotRepository;
  final ClientService clientService;
  final ScheduleService scheduleService;
  final ActivityService activityService;

  public List<AdminSlotResponseDto> findAllSlots() {
    return slotRepository.findAllNonCancelledSlots()
        .stream().map(AdminSlotResponseDto::fromSlot)
        .toList();
  }

  /**
   * Book a list of slots
   *
   * @param slotBatchCreateRequest data for reserving the slots
   * @return list of booked slots
   */
  @Transactional
  public List<Slot> bookSlots(SlotBatchCreateRequest slotBatchCreateRequest) {
    log.info("{} Booking {} slots", LOG_PREFIX,
        Optional.ofNullable(slotBatchCreateRequest.getSlots()).map(List::size).orElse(0));
    Client client = clientService.getOrCreateClient(slotBatchCreateRequest.getClient());

    List<Slot> bookedSlots = slotBatchCreateRequest.getSlots().stream()
        .map(slotDto -> createSlot(slotDto, SlotStatus.BOOKED, client))
        .toList();

    return slotRepository.saveAll(bookedSlots);
  }

  /**
   * Book a single slot
   *
   * @param slotCreateRequest data for reserving a slot
   * @return booked slot
   */
  @Transactional
  public Slot bookSlot(SlotCreateRequest slotCreateRequest) {
    Client client = clientService.getOrCreateClient(slotCreateRequest.getClient());
    Slot slot = createSlot(slotCreateRequest.getSlot(), SlotStatus.BOOKED, client);
    return slotRepository.save(slot);
  }

  /**
   * Block a single slot, without associating any client to it
   *
   * @param slotDto data for blocking a slot
   * @return blocked slot
   */
  @Transactional
  public Slot blockSlot(SlotDto slotDto) {
    log.info("{} Blocking slot, starting at {}", LOG_PREFIX, slotDto.getStartTime());
    Slot slot = createSlot(slotDto, SlotStatus.BLOCKED, null);
    return slotRepository.save(slot);
  }

  /**
   * Patch slot - mostly created to be able to patch slot status
   *
   * @param id           of slot to be patched
   * @param patchSlotDto data to patch with
   */
  @Transactional
  public void patchSlot(Long id, SlotPatchRequest patchSlotDto) {
    log.info("{} Patching slot (id: {})", LOG_PREFIX, id);
    Slot slot = slotRepository.findById(id).orElseThrow();

    if (patchSlotDto.getStatus() != null) {
      if (!SlotStatus.isValidSlotStatusUpdate(slot.getStatus(), patchSlotDto.getStatus())) {
        throw new InvalidOperationException(ErrorMessages.STATUS_UPDATE_INVALID);
      }
      slot.setStatus(patchSlotDto.getStatus());

      slot.getBundledSlots()
          .forEach(bundledSlot -> bundledSlot.setStatus(patchSlotDto.getStatus()));
    }

    if (patchSlotDto.getBookedSpots() != null) {
      validateBookingAvailability(
          slot, slot.getActivity().getCapacity(), patchSlotDto.getBookedSpots()
      );
      slot.setBookedSpots(patchSlotDto.getBookedSpots());
    }
  }

  /**
   * Validates that there are enough spots for the booking to succeed
   *
   * @param slot            to book spots on
   * @param maximumCapacity maximum slots that can be booked during current schedule
   */
  private void validateBookingAvailability(Slot slot, int maximumCapacity) {
    validateBookingAvailability(slot, maximumCapacity, slot.getBookedSpots());
  }

  /**
   * Validates that there are enough spots for the booking to succeed
   *
   * @param slot            to book spots on
   * @param maximumCapacity maximum slots that can be booked during current schedule
   */
  private void validateBookingAvailability(Slot slot, int maximumCapacity, int bookedSpots) {
    // Determine how many spots were already booked
    int alreadyBookedSpots = getAlreadyBookedSpots(slot);

    // Validate if maximumCapacity is valid
    if (maximumCapacity - alreadyBookedSpots - bookedSpots < 0) {
      throw new UnavailableSpotException(ErrorMessages.TOO_MANY_SPOTS_TO_BOOK);
    }
  }

  /**
   * Calculate the number of spots booked for a specific slot time range
   *
   * @param slot for which calculation is performed
   * @return number of booked spots booked for overlapping time range
   */
  private int getAlreadyBookedSpots(Slot slot) {
    return slotRepository.getSumOfBookedSpotsForOverlappingTime(
        slot.getId(), slot.getDate(), slot.getStartTime(),
        slot.getEndTime(), slot.getActivity().getId()
    ).orElse(0);
  }

  /**
   * Create a slot and its bundled slots
   *
   * @param slotDto    data for slot creation
   * @param slotStatus status of the slot
   * @param client     client reserving this slot
   * @return created slot
   */
  private Slot createSlot(SlotDto slotDto, SlotStatus slotStatus, Client client) {
    Activity activity = activityService.findActivityById(slotDto.getActivityId());
    Schedule schedule = scheduleService.findCurrentSchedule(slotDto.getDate());

//    scheduleService.validateTimeRangeRespectsSchedule(
//        schedule, slotDto.getDate(), slotDto.getStartTime()
//    );

    Slot slot = slotRepository.save(Slot.create(
        slotDto.getDate(), slotDto.getStartTime(), slotDto.getBookedSpots(),
        slotStatus, activity, schedule, client, null
    ));
    validateBookingAvailability(slot, activity.getCapacity());

    // Activity is BUNDLE -> create slot for each bundled slot
    if (activity.isBundle() && slotStatus != SlotStatus.BLOCKED) {
      validateBundleSlotPayload(slotDto, activity);

      for (var bundledSlotDto : slotDto.getBundledSlots()) {
//        scheduleService.validateTimeRangeRespectsSchedule(
//            schedule, slot.getDate(), bundledSlotDto.getStartTime()
//        );

        Activity bundledActivity = activityService
            .findActivityById(bundledSlotDto.getActivityId());

        Slot bundledSlot = slotRepository.save(Slot.create(
            slotDto.getDate(), bundledSlotDto.getStartTime(), bundledSlotDto.getBookedSpots(),
            slotStatus, bundledActivity, schedule, client, slot
        ));

        validateTimeInRegardToBundleSlot(slot, bundledSlot);
        validateBookingAvailability(bundledSlot, bundledActivity.getCapacity());
        slot.getBundledSlots().add(bundledSlot);
      }
    }
    return slot;
  }

  private void validateTimeInRegardToBundleSlot(Slot slot, Slot bundledSlot) {
    if (slot.getStartTime().isAfter(bundledSlot.getStartTime())
        || slot.getEndTime().isBefore(bundledSlot.getEndTime())) {
      throw new InvalidOperationException(
          ErrorMessages.BUNDLED_SLOTS_DONT_RESPECT_BUNDLE_TIME_RANGE
      );
    }
  }

  private static void validateBundleSlotPayload(SlotDto slotDto, Activity activity) {
    if (CollectionUtils.isEmpty(slotDto.getBundledSlots())) {
      // bad request
      throw new BadRequestException(ErrorMessages.BUNDLED_SLOTS_IN_REQUEST_NOT_FOUND);
    }

    activity
        .getBundledActivities()
        .forEach(activityInBundle -> {
          var requiredBookedSpots = activityInBundle.getSize();
          var bookedSpotsPerActivity = slotDto.getBundledSlots()
              .stream()
              .filter(bundledSlot ->
                  bundledSlot.getActivityId().equals(activityInBundle.getActivity().getId())
              ).count();

          if (requiredBookedSpots != bookedSpotsPerActivity) {
            throw new BadRequestException(ErrorMessages.SHOULD_BOOK_ALL_SLOTS_FROM_BUNDLE);
          }
        });

  }

}
