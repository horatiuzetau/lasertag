package com.hashtag.lasertag.slot.enums;

import java.util.List;

public enum SlotStatus {
  BOOKED, CONFIRMED, BLOCKED, CANCELLED, NON_ATTENDEE;

  public static boolean isValidSlotStatusUpdate(SlotStatus oldStatus, SlotStatus newStatus) {
    return (oldStatus == BOOKED && List.of(CONFIRMED, CANCELLED).contains(newStatus))
        || (List.of(BLOCKED, CONFIRMED).contains(oldStatus) && newStatus == CANCELLED)
        || (List.of(BOOKED, CONFIRMED).contains(oldStatus) && newStatus == NON_ATTENDEE);
  }
}
