package com.example.demo.endpoint.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.example.demo.domain.entity.Movie;
import com.example.demo.domain.entity.Reservation;
import com.example.demo.domain.entity.Room;
import com.example.demo.domain.entity.Seat;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Genre;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.security.JwtService;
import com.example.demo.service.MovieService;
import com.example.demo.service.ProjectionService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class ReservationControllerIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private UserService userService;
  @Autowired private JwtService jwtService;
  @Autowired private MovieService movieService;
  @Autowired private RoomService roomService;
  @Autowired private ProjectionService projectionService;
  @Autowired private ReservationService reservationService;

  private User owner;
  private User otherClient;
  private Reservation reservation;

  @BeforeEach
  void setUp() {
    Movie movie =
        movieService.create(
            Movie.builder()
                .title("Amélie " + System.nanoTime())
                .genre(Genre.ROMANCE)
                .duration(Duration.ofMinutes(122))
                .build());
    Room room = roomService.createRoom("ROOM-" + System.nanoTime(), 10);
    Seat seat = room.getSeats().get(0);
    var proj =
        projectionService.createProjection(
            movie.getId(), room.getId(), Instant.parse("2026-09-01T18:00:00Z"), BigDecimal.TEN);

    owner = registerClient("res-owner@example.com");
    otherClient = registerClient("res-other@example.com");

    reservation =
        reservationService.createReservation(owner.getId(), proj.getId(), Set.of(seat.getId()));
  }

  private User registerClient(String email) {
    return userService.register(
        User.builder()
            .firstName("Test")
            .lastName("Client")
            .birthdate(LocalDate.of(1990, 1, 1))
            .email(email)
            .password("password")
            .phone("+33600000000")
            .role(UserRole.CLIENT)
            .build());
  }

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  private String tokenFor(User user) {
    return jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
  }

  private String tokenForNewUser(UserRole role, String email) {
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

  @Test
  void getReservationsShouldThrow403ForClients() throws Exception {
    mockMvc()
        .perform(get("/reservations").header("Authorization", "Bearer " + tokenFor(owner)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getReservationsShouldReturn200ForManagersAndEmployees() throws Exception {
    String managerToken = tokenForNewUser(UserRole.MANAGER, "res-manager@example.com");
    String employeeToken = tokenForNewUser(UserRole.EMPLOYEE, "res-employee@example.com");

    mockMvc()
        .perform(get("/reservations").header("Authorization", "Bearer " + managerToken))
        .andExpect(status().isOk());
    mockMvc()
        .perform(get("/reservations").header("Authorization", "Bearer " + employeeToken))
        .andExpect(status().isOk());
  }

  @Test
  void getReservationByIdShouldReturn200WhenClientOwnsIt() throws Exception {
    mockMvc()
        .perform(
            get("/reservations/" + reservation.getId())
                .header("Authorization", "Bearer " + tokenFor(owner)))
        .andExpect(status().isOk());
  }

  @Test
  void getReservationByIdShouldThrow403WhenAnotherClientOwnsIt() throws Exception {
    mockMvc()
        .perform(
            get("/reservations/" + reservation.getId())
                .header("Authorization", "Bearer " + tokenFor(otherClient)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getReservationByIdShouldReturn200ForManagersAndEmployees() throws Exception {
    String managerToken = tokenForNewUser(UserRole.MANAGER, "res-by-id-manager@example.com");

    mockMvc()
        .perform(
            get("/reservations/" + reservation.getId())
                .header("Authorization", "Bearer " + managerToken))
        .andExpect(status().isOk());
  }

  @Test
  void putReservationShouldThrow403ForClients() throws Exception {
    mockMvc()
        .perform(
            put("/reservations")
                .header("Authorization", "Bearer " + tokenFor(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void putReservationShouldReturn200ForEmployeesAndManagers() throws Exception {
    Movie movie =
        movieService.create(
            Movie.builder()
                .title("Amélie 2 " + System.nanoTime())
                .genre(Genre.ROMANCE)
                .duration(Duration.ofMinutes(100))
                .build());
    Room room = roomService.createRoom("ROOM-2-" + System.nanoTime(), 10);
    Seat freeSeat = room.getSeats().get(0);
    var proj =
        projectionService.createProjection(
            movie.getId(), room.getId(), Instant.parse("2026-10-01T18:00:00Z"), BigDecimal.TEN);

    String employeeToken = tokenForNewUser(UserRole.EMPLOYEE, "res-put-employee@example.com");
    String payload =
        """
        {"userId": "%s", "projectionId": "%s", "seatIds": ["%s"]}
        """
            .formatted(owner.getId(), proj.getId(), freeSeat.getId());

    mockMvc()
        .perform(
            put("/reservations")
                .header("Authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated());
  }
}
