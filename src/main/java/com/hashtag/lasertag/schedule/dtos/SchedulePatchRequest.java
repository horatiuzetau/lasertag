package com.hashtag.lasertag.schedule.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SchedulePatchRequest {
    Boolean active;
    Boolean main;
}
