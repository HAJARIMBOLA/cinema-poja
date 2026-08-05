package com.example.demo.endpoint.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class MovieControllerIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private UserService userService;
  @Autowired private JwtService jwtService;
  @Autowired private PasswordEncoder passwordEncoder;

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  private String tokenFor(UserRole role, String email) {
    User user =
        userService.register(
            User.builder()
                .firstName("Test")
                .lastName(role.name())
                .birthdate(LocalDate.of(1990, 1, 1))
                .email(email)
                .password("password")
                .phone("+33600000000")
                .role(role)
                .build());
    return jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
  }

  private static final String MOVIE_PAYLOAD =
      """
      {
        "title": "Inception",
        "genre": "SCI_FI",
        "description": "A mind-bending thriller",
        "durationMinutes": 148
      }
      """;

  @Test
  void putMoviesShouldThrow403ForClients() throws Exception {
    String token = tokenFor(UserRole.CLIENT, "movie-client@example.com");

    mockMvc()
        .perform(
            put("/movies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(MOVIE_PAYLOAD))
        .andExpect(status().isForbidden());
  }

  @Test
  void putMoviesShouldThrow403ForEmployees() throws Exception {
    String token = tokenFor(UserRole.EMPLOYEE, "movie-employee@example.com");

    mockMvc()
        .perform(
            put("/movies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(MOVIE_PAYLOAD))
        .andExpect(status().isForbidden());
  }

  @Test
  void putMoviesShouldReturn200ForManagers() throws Exception {
    String token = tokenFor(UserRole.MANAGER, "movie-manager@example.com");

    mockMvc()
        .perform(
            put("/movies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(MOVIE_PAYLOAD))
        .andExpect(status().isCreated());
  }

  @Test
  void getMoviesShouldReturn200ForEveryone() throws Exception {
    mockMvc().perform(get("/movies")).andExpect(status().isOk());
  }
}
