package com.hashtag.lasertag.slot.enums;

import java.util.List;

public enum SlotStatus {
  COMPLETED, BOOKED, CONFIRMED, BLOCKED, CANCELLED, NON_ATTENDEE;

  public static boolean isValidSlotStatusUpdate(SlotStatus oldStatus, SlotStatus newStatus) {
    return (oldStatus == BOOKED && List.of(CONFIRMED, COMPLETED, NON_ATTENDEE, CANCELLED)
        .contains(newStatus))
        || (oldStatus == CONFIRMED && List.of(COMPLETED, NON_ATTENDEE, CANCELLED)
        .contains(newStatus));
  }
}
