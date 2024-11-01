package com.hashtag.lasertag.health;

import com.hashtag.lasertag.setting.SettingService;
import com.hashtag.lasertag.setting.SettingType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@Validated
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthController {

  final SettingService settingService;

  /**
   * Get reservations status
   *
   * @return ok if enabled, ko if disabled
   */
  @GetMapping("/reservations")
  public ResponseEntity<Void> getReservationState() {
    if (settingService.isEnabled(SettingType.BOOKINGS_ENABLED.getName())) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.internalServerError().build();
    }
  }

}