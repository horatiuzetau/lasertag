package com.hashtag.lasertag.slot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashtag.lasertag.activity.Activity;
import com.hashtag.lasertag.client.dtos.ClientDto;
import com.hashtag.lasertag.slot.Slot;
import com.hashtag.lasertag.slot.enums.SlotStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminSlotResponseDto {

  @Getter
  int bookedSpots;

  @Getter
  Activity activity;

  @Getter
  ClientDto client;

  @Getter
  @JsonProperty("Id")
  Long id;

  @Getter
  @JsonProperty("Subject")
  String subject;

  @Getter
  @JsonProperty("StartTime")
  LocalDateTime startTime;

  @Getter
  @JsonProperty("EndTime")
  LocalDateTime endtime;

  @JsonProperty("ParentId")
  Long parentId;

  @JsonProperty("IsBlock")
  Boolean isBlock;

  @JsonProperty("Status")
  SlotStatus status;

  @Getter
  Double price;

  public static AdminSlotResponseDto fromSlot(Slot slot) {
    AdminSlotResponseDto adminSlotResponseDto = new AdminSlotResponseDto();
    adminSlotResponseDto.setId(slot.getId());
    adminSlotResponseDto.setSubject(slot.getActivity().getName());
    adminSlotResponseDto.setStartTime(LocalDateTime.of(slot.getDate(), slot.getStartTime()));
    adminSlotResponseDto.setEndtime(LocalDateTime.of(slot.getDate(), slot.getEndTime()));
    adminSlotResponseDto.setBookedSpots(slot.getBookedSpots());
    adminSlotResponseDto.setActivity(slot.getActivity());
    adminSlotResponseDto.setClient(ClientDto.fromClient(slot.getClient()));
    adminSlotResponseDto.setStatus(slot.getStatus());
    adminSlotResponseDto.setPrice(slot.getPrice());
    Optional.ofNullable(slot.getBundleSlot())
            .ifPresent(bundleSlot -> adminSlotResponseDto.setParentId(bundleSlot.getId()));

//    if (slot.getStatus() == SlotStatus.BLOCKED) {
//      adminSlotResponseDto.setIsBlock(Boolean.TRUE);
//    }

    return adminSlotResponseDto;
  }


}
