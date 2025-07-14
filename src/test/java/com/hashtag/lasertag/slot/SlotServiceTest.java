package com.hashtag.lasertag.slot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hashtag.lasertag.activity.Activity;
import com.hashtag.lasertag.activity.ActivityService;
import com.hashtag.lasertag.activity.enums.ActivityType;
import com.hashtag.lasertag.client.Client;
import com.hashtag.lasertag.client.ClientService;
import com.hashtag.lasertag.client.dtos.ClientCreateUpdateRequest;
import com.hashtag.lasertag.schedule.Schedule;
import com.hashtag.lasertag.schedule.ScheduleService;
import com.hashtag.lasertag.shared.TimeRange;
import com.hashtag.lasertag.shared.exceptions.ErrorMessages;
import com.hashtag.lasertag.slot.dtos.SlotBatchCreateRequest;
import com.hashtag.lasertag.slot.dtos.SlotDto;
import com.hashtag.lasertag.slot.enums.SlotStatus;
import com.hashtag.lasertag.slot.exceptions.UnavailableSpotException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {

  @Mock
  SlotRepository slotRepository;
  @Mock
  ClientService clientService;
  @Mock
  ScheduleService scheduleService;
  @Mock
  ActivityService activityService;

  @InjectMocks
  SlotService slotService;

  /*
    Client doesn't exist

   */
  @Test
  public void bookSlots_whenSingleSlot_ShouldSucceed() {
    // Request payloads
    int bookedSpots = 3;
    int alreadyBookedSpots = 0;
    long activityId = 1L;
    LocalTime startTime = LocalTime.of(10, 0);
    var slotDto = createSlotDto(bookedSpots, activityId, startTime);
    var clientCreateUpdateRequest = createClientCreateUpdateRequest(
        true, true, true
    );
    var slotBatchCreateRequest = new SlotBatchCreateRequest(
        List.of(slotDto), clientCreateUpdateRequest
    );

    // Mocked returns
    Client client = createClient(clientCreateUpdateRequest);
    Activity activity = createActivity();
    Schedule schedule = createSchedule();
    Slot slot = Slot.create(
        slotDto.getDate(), slotDto.getStartTime(), slotDto.getBookedSpots(),
        SlotStatus.BOOKED, activity, schedule, client, null
    );
    slot.setId(1L);

    // Mock
    when(clientService.getOrCreateClient(clientCreateUpdateRequest))
        .thenReturn(client);
    when(activityService.findActivityById(activityId))
        .thenReturn(activity);
    when(scheduleService.findCurrentSchedule(slotDto.getDate()))
        .thenReturn(schedule);

    // listen to this - what has been used for saving
    when(slotRepository.save(any()))
        .thenReturn(slot);
    when(slotRepository.getSumOfBookedSpotsForOverlappingTime(
        slot.getId(), slot.getDate(), slot.getStartTime(), slot.getEndTime(),
        slot.getActivity().getId()
    )).thenReturn(Optional.of(alreadyBookedSpots));
    // listen to this - what has been used for saving
    when(slotRepository.saveAll(any()))
        .thenReturn(List.of(slot));

    // Test
    slotService.bookSlots(slotBatchCreateRequest);

    // Verify
    ArgumentCaptor<Slot> slotArgumentCaptor = ArgumentCaptor.forClass(Slot.class);
    verify(slotRepository).save(slotArgumentCaptor.capture());
    Slot capturedSlot = slotArgumentCaptor.getValue();

    assertEquals(SlotStatus.BOOKED, capturedSlot.getStatus());
    assertEquals(bookedSpots, capturedSlot.getBookedSpots());
    assertEquals(activityId, capturedSlot.getActivity().getId());
    assertEquals(startTime, capturedSlot.getStartTime());
    assertEquals(activity.getPrice(), capturedSlot.getPrice());
    assertEquals(clientCreateUpdateRequest.getEmail(), capturedSlot.getClient().getEmail());
    assertEquals(clientCreateUpdateRequest.getPhone(), capturedSlot.getClient().getPhone());
  }

  @Test
  public void bookSlots_whenSingleSlotAndNotEnoughSpotsAvailable_ShouldFailToUnavailableSpotException() {
    // Request payloads
    int bookedSpots = 3;
    int alreadyBookedSpots = 4;
    long activityId = 1L;
    LocalTime startTime = LocalTime.of(10, 0);
    var slotDto = createSlotDto(bookedSpots, activityId, startTime);
    var clientCreateUpdateRequest = createClientCreateUpdateRequest(
        true, true, true
    );
    var slotBatchCreateRequest = new SlotBatchCreateRequest(
        List.of(slotDto), clientCreateUpdateRequest
    );

    // Mocked returns
    Client client = createClient(clientCreateUpdateRequest);
    Activity activity = createActivity();
    Schedule schedule = createSchedule();
    Slot slot = Slot.create(
        slotDto.getDate(), slotDto.getStartTime(), slotDto.getBookedSpots(),
        SlotStatus.BOOKED, activity, schedule, client, null
    );
    slot.setId(1L);

    // Mock
    when(clientService.getOrCreateClient(clientCreateUpdateRequest))
        .thenReturn(client);
    when(activityService.findActivityById(activityId))
        .thenReturn(activity);
    when(scheduleService.findCurrentSchedule(slotDto.getDate()))
        .thenReturn(schedule);

    // listen to this - what has been used for saving
    when(slotRepository.save(any()))
        .thenReturn(slot);
    when(slotRepository.getSumOfBookedSpotsForOverlappingTime(
        slot.getId(), slot.getDate(), slot.getStartTime(), slot.getEndTime(),
        slot.getActivity().getId()
    )).thenReturn(Optional.of(alreadyBookedSpots));

    // Test & Verify
    Exception ex = assertThrows(
        UnavailableSpotException.class,
        () -> slotService.bookSlots(slotBatchCreateRequest)
    );

    assertEquals(ErrorMessages.TOO_MANY_SPOTS_TO_BOOK, ex.getMessage());
  }


  private Schedule createSchedule() {
    Schedule schedule = new Schedule();
    schedule.setId(1L);
    schedule.setActive(true);
    schedule.setMain(true);
    schedule.setName("Default Schedule");
    schedule.setDescription("Description");
    schedule.setMonday(Arrays.asList(new TimeRange(LocalTime.of(8, 0), LocalTime.of(14, 0))));
    schedule.setTuesday(Arrays.asList(new TimeRange(LocalTime.of(12, 0), LocalTime.of(22, 0))));
    return schedule;
  }

  private SlotDto createSlotDto(int bookedSpots, long activityId, LocalTime startTime) {
    SlotDto slotDto = new SlotDto();
    slotDto.setDate(LocalDate.of(2025, 7, 14));
    slotDto.setStartTime(startTime);
    slotDto.setBookedSpots(bookedSpots);
    slotDto.setActivityId(activityId);
    return slotDto;
  }

  private Client createClient(ClientCreateUpdateRequest clientCreateUpdateRequest) {
    Client client = new Client();
    client.setFirstName(clientCreateUpdateRequest.getFirstName());
    client.setLastName(clientCreateUpdateRequest.getLastName());
    client.setPhone(clientCreateUpdateRequest.getPhone());
    client.setEmail(clientCreateUpdateRequest.getEmail());
    client.setSubscribedToNewsletter(clientCreateUpdateRequest.isSubscribedToNewsletter());
    return client;
  }

  private ClientCreateUpdateRequest createClientCreateUpdateRequest(boolean gdpr,
      boolean subscribedToNewsletter, boolean termsAndConditions) {

    ClientCreateUpdateRequest clientRequest = new ClientCreateUpdateRequest();
    clientRequest.setPhone("0777777777");
    clientRequest.setEmail("test@test.com");
    clientRequest.setFirstName("First Name");
    clientRequest.setLastName("Last Name");
    clientRequest.setGdpr(gdpr);
    clientRequest.setSubscribedToNewsletter(subscribedToNewsletter);
    clientRequest.setTermsAndConditions(termsAndConditions);
    return clientRequest;
  }

  private Activity createActivity() {
    Activity activity = new Activity();
    activity.setId(1L);
    activity.setActive(true);
    activity.setType(ActivityType.SINGLE);
    activity.setShareable(true);
    activity.setCapacity(6);
    activity.setName("Laser Tag");
    activity.setPrice(20.00);
    activity.setDuration(20);
    activity.setRecoveryTime(10);
    return activity;
  }
}