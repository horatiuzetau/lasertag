package com.hashtag.lasertag.slot;

import com.hashtag.lasertag.setting.SettingService;
import com.hashtag.lasertag.setting.SettingType;
import com.hashtag.lasertag.setting.exceptions.ServiceUnavailableException;
import com.hashtag.lasertag.shared.exceptions.ErrorMessages;
import com.hashtag.lasertag.slot.dtos.AdminSlotResponseDto;
import com.hashtag.lasertag.slot.dtos.SlotBatchCreateRequest;
import com.hashtag.lasertag.slot.dtos.SlotDto;
import com.hashtag.lasertag.slot.dtos.SlotPatchRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/slots")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotController {

  final SlotService slotService;
  final SettingService settingService;

  /**
   * Endpoint to get all slots
   */
  @GetMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<AdminSlotResponseDto>> getAllSlots() {
    return ResponseEntity.ok(slotService.findAllSlots());
  }

  /**
   * Endpoint to create a new slot.
   *
   * @param slotBatchCreateRequest the slot data to create
   * @return the created slot
   */
  @PostMapping("/batch")
  public ResponseEntity<List<Slot>> bookSlots(
      @Valid @RequestBody SlotBatchCreateRequest slotBatchCreateRequest) {

    validateServerAvailability();
    List<Slot> slots = slotService.bookSlots(slotBatchCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(slots);
  }

//  /**
//   * Endpoint to create a new slot.
//   *
//   * @param slotCreateRequest the slot data to create
//   * @return the created slot
//   */
//  @PostMapping
//  public ResponseEntity<Slot> bookSlot(@Valid @RequestBody SlotCreateRequest slotCreateRequest) {
//    validateServerAvailability();
//    Slot slot = slotService.bookSlot(slotCreateRequest);
//    return ResponseEntity.status(HttpStatus.CREATED).body(slot);
//  }

  /**
   * Endpoint to block a slot (e.g., for maintenance or unavailability).
   *
   * @param slotDto the slot data to block
   * @return the blocked slot
   */
  @PostMapping("/block")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Slot> blockSlot(@Valid @RequestBody SlotDto slotDto) {
    Slot blockedSlot = slotService.blockSlot(slotDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(blockedSlot);
  }

  /**
   * Patch a slot.
   *
   * @param id           the slot ID
   * @param patchSlotDto the slot data to update
   * @return the updated slot
   */
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Void> patchSlot(@PathVariable Long id,
      @Valid @RequestBody SlotPatchRequest patchSlotDto) {
    slotService.patchSlot(id, patchSlotDto);
    return ResponseEntity.noContent().build();
  }

  private void validateServerAvailability() {
    if (!settingService.isEnabled(SettingType.BOOKINGS_ENABLED.getName())) {
      throw new ServiceUnavailableException(ErrorMessages.SERVICE_UNAVAILABLE);
    }
  }
}