package com.hashtag.lasertag.activity.dtos;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivityPatchRequest {

    Boolean active;

    List<BundleActivityDto> bundledActivities;
}
