package com.hashtag.lasertag.client.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
public class ClientCreateUpdateRequest {

    @Size(max = 500, message = "First name cannot be longer than 500 characters")
    String firstName;

    @Size(max = 500, message = "Last name cannot be longer than 500 characters")
    String lastName;

    @Email(message = "Email should be valid")
    @Size(max = 500, message = "Email cannot be longer than 500 characters")
    String email;

    @Size(max = 50, message = "Phone number cannot be longer than 50 characters")
    String phone;

    boolean subscribedToNewsletter = false;

    boolean termsAndConditions = false;

    boolean gdpr;

}