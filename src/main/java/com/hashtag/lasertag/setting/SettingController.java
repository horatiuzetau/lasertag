package com.hashtag.lasertag.setting;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingController {

  final SettingService settingService;

  @PostMapping("/reservations/activate")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> getAllSlots(@RequestParam boolean active) {
    settingService.patchReservationsStatus(active);
    return ResponseEntity.ok().body(null);
  }

}
