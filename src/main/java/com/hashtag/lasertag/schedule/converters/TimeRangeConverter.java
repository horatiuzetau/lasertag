package com.hashtag.lasertag.schedule.converters;

import com.hashtag.lasertag.shared.exceptions.ErrorMessages;
import com.hashtag.lasertag.shared.exceptions.InternalException;
import com.hashtag.lasertag.shared.TimeRange;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class TimeRangeConverter implements AttributeConverter<List<TimeRange>, String> {

  @Override
  public String convertToDatabaseColumn(List<TimeRange> timeRanges) {
    if (timeRanges == null || timeRanges.isEmpty()) {
      return null;
    }

    StringBuilder result = new StringBuilder("{");
    for (TimeRange timeRange : timeRanges) {
      result
          .append("\"(")
          .append(timeRange.getStartTime())
          .append(",")
          .append(timeRange.getEndTime())
          .append(")\",");
    }

    // Remove the last comma
    result.setLength(result.length() - 1);
    result.append("}");
    return result.toString();
  }

  @Override
  public List<TimeRange> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return null;
    }
    // Remove curly braces and split by commas
    String[] timeRangeStrings = dbData
        .replace("{", "").replace("}", "")
        .replace("\"", "").replace("\"", "")
        .split("\\),\\(");

    List<TimeRange> timeRanges = new ArrayList<>();
    for (String timeRangeString : timeRangeStrings) {
      String[] times = timeRangeString
          .replace("(", "")
          .replace(")", "")
          .split(",");

      if (times.length != 2) {
        throw new InternalException(ErrorMessages.INTERNAL_SERVER_ERROR);
      }

      LocalTime startTime = convertToLocalTime(times[0].trim());
      LocalTime endTime = convertToLocalTime(times[1].trim());
      timeRanges.add(new TimeRange(startTime, endTime));
    }
    return timeRanges;
  }

  private LocalTime convertToLocalTime(String date) {
    try {
      return LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm"));
    } catch (Exception e) {
      return LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
  }
}