package com.hashtag.lasertag.client;

import com.hashtag.lasertag.client.dtos.ClientPatchRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientController {

    final ClientService clientService;

    /**
     * Get a list of all client emails.
     *
     * @return ResponseEntity with the list of client emails
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/emails")
    public ResponseEntity<List<String>> getAllClientEmails() {
        List<String> emails = clientService.getAllClientEmails();
        return ResponseEntity.ok(emails);
    }

    /**
     * Get the number of clients subscribed to the newsletter.
     *
     * @return ResponseEntity with the number of subscribed clients
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchClient(@PathVariable Long id,  @Valid @RequestBody ClientPatchRequest clientPatchRequest) {
        clientService.patchClient(id, clientPatchRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Resets data of client. The client remains in the database to count.
     *
     * @return ResponseEntity with the number of subscribed clients
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> resetClient(@PathVariable Long id) {
        clientService.resetClient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the total number of clients.
     *
     * @return ResponseEntity with the number of clients
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<Long> getNumberOfClients() {
        long count = clientService.getNumberOfClients();
        return ResponseEntity.ok(count);
    }

    /**
     * Get the number of clients subscribed to the newsletter.
     *
     * @return ResponseEntity with the number of subscribed clients
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/count/subscribed")
    public ResponseEntity<Long> getNumberOfSubscribedClients() {
        long subscribedCount = clientService.getNumberOfSubscribedClients();
        return ResponseEntity.ok(subscribedCount);
    }


}