package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.dao.UserRepository;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.service.exception.ConflictException;
import com.example.demo.service.exception.NotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  private User aClient() {
    return User.builder()
        .firstName("Jean")
        .lastName("Dupont")
        .birthdate(LocalDate.of(1990, 1, 1))
        .email("jean.dupont@example.com")
        .password("plain-password")
        .phone("+33612345678")
        .role(UserRole.CLIENT)
        .build();
  }

  @Test
  void shouldHashPasswordBeforeSaving() {
    User user = aClient();
    when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
    when(passwordEncoder.encode("plain-password")).thenReturn("hashed-password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User saved = userService.register(user);

    assertThat(saved.getPassword()).isEqualTo("hashed-password");
  }

  @Test
  void shouldRejectDuplicateEmail() {
    User user = aClient();
    when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

    assertThatThrownBy(() -> userService.register(user)).isInstanceOf(ConflictException.class);
  }

  @Test
  void shouldThrowWhenUserNotFoundById() {
    UUID id = UUID.randomUUID();
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getById(id)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldThrowWhenUserNotFoundByEmail() {
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getByEmail("missing@example.com"))
        .isInstanceOf(NotFoundException.class);
  }
}
