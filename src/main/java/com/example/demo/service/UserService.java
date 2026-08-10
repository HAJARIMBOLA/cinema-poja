package com.example.demo.service;

import com.example.demo.dao.UserRepository;
import com.example.demo.domain.entity.User;
import com.example.demo.service.exception.ConflictException;
import com.example.demo.service.exception.NotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public User register(User user) {
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new ConflictException("Email '" + user.getEmail() + "' is already in use");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public User getById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
  }

  public User getByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User with email '" + email + "' not found"));
  }
}
