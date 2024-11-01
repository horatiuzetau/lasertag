package com.hashtag.lasertag.availability;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailabilityController {

  final AvailabilityService availabilityService;

  @GetMapping
  public Object getAvailability(@RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date) {
     return availabilityService.getAvailability(date);
  }

}
