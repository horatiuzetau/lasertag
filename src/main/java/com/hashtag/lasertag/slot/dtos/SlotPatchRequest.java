package com.hashtag.lasertag.slot.dtos;

import com.hashtag.lasertag.slot.enums.SlotStatus;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class SlotPatchRequest {

    SlotStatus status;

    @Min(value = 1, message = "Can't book less than 1 spots")
    Integer bookedSpots;

}
