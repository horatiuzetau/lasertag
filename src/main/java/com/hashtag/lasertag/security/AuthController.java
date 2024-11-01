package com.hashtag.lasertag.security;

import com.hashtag.lasertag.security.dtos.AuthRequest;
import com.hashtag.lasertag.security.dtos.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
    return ResponseEntity.ok(authService.login(authRequest));
  }

//  @PostMapping("/register")
//  public String register(@RequestBody @Valid AuthRequest authRequest) {
//    return authService.register(authRequest);
//  }
}