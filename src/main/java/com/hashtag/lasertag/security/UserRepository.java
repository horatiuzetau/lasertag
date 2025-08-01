package com.hashtag.lasertag.security;

import com.hashtag.lasertag.security.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u WHERE u.username = :username")
  Optional<User> findByUsername(String username);
}
