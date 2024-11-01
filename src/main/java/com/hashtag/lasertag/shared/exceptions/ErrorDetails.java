package com.hashtag.lasertag.shared.exceptions;

import com.hashtag.lasertag.shared.enums.ResponseVoiceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

  int statusCode;
  String message;
  String details;
  ResponseVoiceType type;

}