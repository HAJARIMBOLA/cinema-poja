package com.example.demo.endpoint.rest.controller;

import com.example.demo.domain.entity.User;
import com.example.demo.endpoint.rest.dto.AuthResponse;
import com.example.demo.endpoint.rest.dto.LoginRequest;
import com.example.demo.endpoint.rest.dto.RegisterRequest;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    User user =
        userService.register(
            User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthdate(request.birthdate())
                .email(request.email())
                .password(request.password())
                .phone(request.phone())
                .role(request.role())
                .build());

    String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
    return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    User user;
    try {
      user = userService.getByEmail(request.email());
    } catch (com.example.demo.service.exception.NotFoundException e) {
      throw new BadCredentialsException("Invalid email or password");
    }

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
    return ResponseEntity.ok(new AuthResponse(token));
  }
}
