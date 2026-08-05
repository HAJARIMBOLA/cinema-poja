package com.example.demo.endpoint.rest.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.example.demo.domain.entity.Movie;
import com.example.demo.domain.entity.Room;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Genre;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.security.JwtService;
import com.example.demo.service.MovieService;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class ProjectionControllerIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private UserService userService;
  @Autowired private JwtService jwtService;
  @Autowired private MovieService movieService;
  @Autowired private RoomService roomService;

  private Movie movie;
  private Room room;

  @BeforeEach
  void setUp() {
    movie =
        movieService.create(
            Movie.builder()
                .title("Inception " + System.nanoTime())
                .genre(Genre.SCI_FI)
                .duration(Duration.ofMinutes(148))
                .build());
    room = roomService.createRoom("ROOM-" + System.nanoTime(), 20);
  }

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  private String tokenFor(UserRole role, String email) {
    User user =
        userService.register(
            User.builder()
                .firstName("Test")
                .lastName(role.name())
                .birthdate(LocalDate.of(1990, 1, 1))
                .email(System.nanoTime() + "-" + email)
                .password("password")
                .phone("+33600000000")
                .role(role)
                .build());
    return jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
  }

  private String projectionPayload() {
    return """
           {
             "movieId": "%s",
             "roomId": "%s",
             "datetime": "2026-09-01T18:00:00Z",
             "seatPrice": 12.5
           }
           """
        .formatted(movie.getId(), room.getId());
  }

  @Test
  void putProjectionShouldThrow403ForClients() throws Exception {
    String token = tokenFor(UserRole.CLIENT, "proj-client@example.com");

    mockMvc()
        .perform(
            put("/projections")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectionPayload()))
        .andExpect(status().isForbidden());
  }

  @Test
  void putProjectionShouldThrow403ForEmployees() throws Exception {
    String token = tokenFor(UserRole.EMPLOYEE, "proj-employee@example.com");

    mockMvc()
        .perform(
            put("/projections")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectionPayload()))
        .andExpect(status().isForbidden());
  }

  @Test
  void putProjectionShouldReturn200ForManagers() throws Exception {
    String token = tokenFor(UserRole.MANAGER, "proj-manager@example.com");

    mockMvc()
        .perform(
            put("/projections")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectionPayload()))
        .andExpect(status().isCreated());
  }

  @Test
  void getProjectionsShouldReturn200ForEveryone() throws Exception {
    mockMvc().perform(get("/projections")).andExpect(status().isOk());
  }
}
