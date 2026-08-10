package com.example.demo.endpoint.rest.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class RoomControllerIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private UserService userService;
  @Autowired private JwtService jwtService;

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
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

  private String roomPayload() {
    return """
           {
             "number": "%s",
             "capacity": 5
           }
           """
        .formatted("ROOM-" + System.nanoTime());
  }

  @Test
  void putRoomsShouldThrow403ForClients() throws Exception {
    String token = tokenFor(UserRole.CLIENT, "room-client@example.com");

    mockMvc()
        .perform(
            put("/rooms")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomPayload()))
        .andExpect(status().isForbidden());
  }

  @Test
  void putRoomsShouldThrow403ForEmployees() throws Exception {
    String token = tokenFor(UserRole.EMPLOYEE, "room-employee@example.com");

    mockMvc()
        .perform(
            put("/rooms")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomPayload()))
        .andExpect(status().isForbidden());
  }

  @Test
  void putRoomsShouldReturn201ForManagersAndGenerateOneSeatPerCapacityUnit() throws Exception {
    String token = tokenFor(UserRole.MANAGER, "room-manager@example.com");

    mockMvc()
        .perform(
            put("/rooms")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomPayload()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.capacity").value(5))
        .andExpect(jsonPath("$.seats.length()").value(5));
  }

  @Test
  void putRoomsShouldThrow409OnDuplicateNumber() throws Exception {
    String token = tokenFor(UserRole.MANAGER, "room-duplicate@example.com");
    String payload = roomPayload();

    mockMvc()
        .perform(
            put("/rooms")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated());

    mockMvc()
        .perform(
            put("/rooms")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isConflict());
  }

  @Test
  void getRoomsShouldReturn200ForEveryone() throws Exception {
    mockMvc().perform(get("/rooms")).andExpect(status().isOk());
  }

  @Test
  void getRoomByUnknownIdShouldReturn404() throws Exception {
    mockMvc().perform(get("/rooms/" + UUID.randomUUID())).andExpect(status().isNotFound());
  }
}
