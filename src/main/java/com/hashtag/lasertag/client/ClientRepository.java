package com.hashtag.lasertag.client;

import com.hashtag.lasertag.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c.email FROM Client c WHERE c.email IS NOT NULL")
    List<String> findAllEmails();

    @Query("""
            SELECT c
            FROM Client c
            WHERE (:email IS NOT NULL AND LOWER(c.email) like LOWER(:email))
                OR (:phone IS NOT NULL AND LOWER(c.phone) like LOWER(:phone))
            """)
    Optional<Client> findByEmailOrPhone(String email, String phone);

    long countBySubscribedToNewsletterTrue();

}
