package com.hashtag.lasertag.client;

import com.hashtag.lasertag.client.dtos.ClientCreateUpdateRequest;
import com.hashtag.lasertag.client.dtos.ClientPatchRequest;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientService {

  final ClientRepository clientRepository;

  /**
   * Get a list of all client emails.
   *
   * @return list of client emails
   */
  public List<String> getAllClientEmails() {
    return clientRepository.findAllEmails();
  }

  /**
   * Searches if client already exists. If yes, update the data and set it for the slot. If not,
   * create a new one and set it for the slot.
   *
   * @param clientCreateUpdateRequest to create/update client data with
   */
  public Client getOrCreateClient(ClientCreateUpdateRequest clientCreateUpdateRequest) {
    return findByEmailOrPhone(clientCreateUpdateRequest.getEmail(), clientCreateUpdateRequest.getEmail())
        .map(existingClient -> updateClientData(existingClient, clientCreateUpdateRequest))
        .orElseGet(() -> createClient(clientCreateUpdateRequest));
  }

  private Optional<Client> findByEmailOrPhone(String email, String phone) {
    return clientRepository.findByEmailOrPhone(
        Optional.ofNullable(email).orElse(""),
        Optional.ofNullable(phone).orElse("")
    );
  }

  private Client createClient(ClientCreateUpdateRequest clientCreateUpdateRequest) {
    Client client = new Client();
    updateClientData(client, clientCreateUpdateRequest);
    return clientRepository.save(client);
  }

  private Client updateClientData(Client client, ClientCreateUpdateRequest clientCreateUpdateRequest) {
    client.setFirstName(StringUtils.capitalize(StringUtils.trim(clientCreateUpdateRequest.getFirstName())));
    client.setLastName(StringUtils.capitalize(StringUtils.trim(clientCreateUpdateRequest.getLastName())));
    client.setPhone(StringUtils.trim(clientCreateUpdateRequest.getPhone()));
    client.setEmail(StringUtils.trim(clientCreateUpdateRequest.getEmail()));
    client.setSubscribedToNewsletter(clientCreateUpdateRequest.isSubscribedToNewsletter());
    return clientRepository.save(client);
  }

  /**
   * Patch client data by client ID.
   *
   * @param id             the ID of the client to patch
   * @param clientPatchRequest the DTO containing the fields to update
   */
  @Transactional
  public void patchClient(Long id, ClientPatchRequest clientPatchRequest) {
    Client client = clientRepository.findById(id).orElseThrow();

    // Update the fields based on the DTO
    if (clientPatchRequest.getFirstName() != null) {
      client.setFirstName(clientPatchRequest.getFirstName());
    }
    if (clientPatchRequest.getLastName() != null) {
      client.setLastName(clientPatchRequest.getLastName());
    }
    if (clientPatchRequest.getEmail() != null) {
      client.setEmail(clientPatchRequest.getEmail());
    }
    if (clientPatchRequest.getPhone() != null) {
      client.setPhone(clientPatchRequest.getPhone());
    }
    if (clientPatchRequest.getSubscribedToNewsletter() != null) {
      client.setSubscribedToNewsletter(clientPatchRequest.getSubscribedToNewsletter());
    }
  }

  /**
   * Resets client data
   *
   * @param id of client to reset
   */
  @Transactional
  public void resetClient(Long id) {
    Client client = clientRepository.findById(id).orElseThrow();
    client.setFirstName(null);
    client.setLastName(null);
    client.setPhone(null);
    client.setEmail(null);
    client.setSubscribedToNewsletter(false);
  }

  /**
   * Get the total number of clients.
   *
   * @return number of clients
   */
  public long getNumberOfClients() {
    return clientRepository.count();
  }

  /**
   * Get the number of clients subscribed to the newsletter.
   *
   * @return number of subscribed clients
   */
  public long getNumberOfSubscribedClients() {
    return clientRepository.countBySubscribedToNewsletterTrue();
  }

}