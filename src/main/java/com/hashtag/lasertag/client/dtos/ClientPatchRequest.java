package com.hashtag.lasertag.client.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientPatchRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean subscribedToNewsletter;
}
