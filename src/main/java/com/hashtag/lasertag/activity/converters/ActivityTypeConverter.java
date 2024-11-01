package com.hashtag.lasertag.activity.converters;

import com.hashtag.lasertag.activity.enums.ActivityType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ActivityTypeConverter implements AttributeConverter<ActivityType, String> {

  @Override
  public String convertToDatabaseColumn(ActivityType status) {
    if (status == null) {
      return null;
    }
    return status.name();
  }

  @Override
  public ActivityType convertToEntityAttribute(String status) {
    if (status == null) {
      return null;
    }
    return ActivityType.valueOf(status);
  }
}
