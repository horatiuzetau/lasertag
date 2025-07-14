package com.hashtag.lasertag.setting.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettingType {
    BOOKINGS_ENABLED("bookings.enabled");

    final String name;
}
