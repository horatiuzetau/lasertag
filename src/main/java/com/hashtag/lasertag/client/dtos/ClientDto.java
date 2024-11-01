package com.hashtag.lasertag.client.dtos;

import com.hashtag.lasertag.client.Client;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientDto {
  Long id;
  String firstName;
  String lastName;
  String email;
  String phone;

  public static ClientDto fromClient(Client client) {
    if (client == null) {
      return null;
    }

    ClientDto clientDto = new ClientDto();
    clientDto.setId(client.getId());
    clientDto.setEmail(client.getEmail());
    clientDto.setPhone(client.getPhone());
    clientDto.setFirstName(client.getFirstName());
    clientDto.setLastName(client.getLastName());
    return clientDto;
  }
}
