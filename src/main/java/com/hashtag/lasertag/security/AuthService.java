package com.hashtag.lasertag.security;

import com.hashtag.lasertag.security.dtos.AuthRequest;
import com.hashtag.lasertag.security.dtos.AuthResponse;
import com.hashtag.lasertag.security.jwt.JwtUtil;
import com.hashtag.lasertag.security.models.Role;
import com.hashtag.lasertag.security.models.User;
import com.hashtag.lasertag.shared.exceptions.BadRequestException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthService {

  final static String ADMIN_ROLE = "ADMIN";
  final UserRepository userRepository;
  final RoleRepository roleRepository;
  final PasswordEncoder passwordEncoder;
  final AuthenticationManager authenticationManager;
  final UserDetailsService userDetailsService;
  final JwtUtil jwtUtil;

  public AuthResponse login(AuthRequest authRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            authRequest.getUsername(), authRequest.getPassword()
        )
    );

    UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

    return new AuthResponse(
        authRequest.getUsername(),
        jwtUtil.generateToken(userDetails.getUsername())
    );
  }

  @Transactional
  public String register(AuthRequest authRequest) {
    if (!userRepository.findAll().isEmpty()) {
      throw new BadRequestException("User deja exista");
    }

    Role role = getOrCreateAdminRole();
    User user = createUser(authRequest, role);
    return user.getUsername();
  }

  private Role getOrCreateAdminRole() {
    return roleRepository.findByName(ADMIN_ROLE)
        .orElseGet(() -> {
          Role role = new Role();
          role.setName(ADMIN_ROLE);
          return roleRepository.save(role);
        });
  }

  private User createUser(AuthRequest authRequest, Role role) {
    User user = new User();
    user.setUsername(authRequest.getUsername());
    user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
    user.setRole(role);
    return userRepository.save(user);
  }
}
