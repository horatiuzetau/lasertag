package com.hashtag.lasertag.activity.dtos;

import com.hashtag.lasertag.activity.enums.ActivityType;
import com.hashtag.lasertag.activity.Activity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityRequest {

    Long id;

    @NotNull
    @Size(min = 1, max = 100, message = "Activity should have a name")
    String name;

    @NotNull
    @Min(value = 0, message = "Duration must be equal or greater than 0")
    int duration;

    @NotNull
    @Min(value = 0, message = "Recovery time must be equal or greater than 0")
    int recoveryTime;

    @NotNull
    @Positive(message = "Capacity must be greater than 0")
    int capacity;

    @NotNull
    @Positive(message = "Price must be greater than 0")
    double price;

    @NotNull
    @Enumerated(EnumType.STRING)
    ActivityType type;

    @Valid
    List<BundleActivityDto> bundledActivities;

    boolean active = false;

    boolean shareable = false;

    public Activity toActivity() {
        Activity activity = new Activity();
        activity.setName(this.name);
        activity.setDuration(this.duration);
        activity.setRecoveryTime(this.recoveryTime);
        activity.setCapacity(this.capacity);
        activity.setPrice(this.price);
        activity.setType(this.type);
        activity.setActive(this.active);
        activity.setShareable(this.shareable);
        return activity;
    }

}
