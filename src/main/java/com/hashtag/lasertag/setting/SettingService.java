package com.hashtag.lasertag.setting;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SettingService {

  final SettingRepository settingRepository;

  public boolean isEnabled(String name) {
    return settingRepository.findByName(name)
        .map(setting -> Boolean.valueOf(setting.getValue()))
        .orElse(false);
  }

  @Transactional
  public void patchReservationsStatus(boolean active) {
    var setting = settingRepository.findByName(SettingType.BOOKINGS_ENABLED.getName())
        .orElseThrow();
    setting.setValue(String.valueOf(active));
    settingRepository.save(setting);
  }
}
