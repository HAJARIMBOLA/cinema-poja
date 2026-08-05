package com.example.demo.endpoint.rest.controller;

import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.conf.FacadeIT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class AuthControllerIT extends FacadeIT {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
  }

  private String registerPayload(String email, String role) {
    return """
           {
             "firstName": "Jean",
             "lastName": "Dupont",
             "birthdate": "1990-01-01",
             "email": "%s",
             "password": "s3cret-password",
             "phone": "+33612345678",
             "role": "%s"
           }
           """
        .formatted(email, role);
  }

  @Test
  void shouldRegisterAndReturnAJwt() throws Exception {
    mockMvc()
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload("register-ok@example.com", "CLIENT")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value(not("")));
  }

  @Test
  void shouldRejectRegistrationWithDuplicateEmail() throws Exception {
    mockMvc()
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload("duplicate@example.com", "CLIENT")))
        .andExpect(status().isCreated());

    mockMvc()
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload("duplicate@example.com", "CLIENT")))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldLoginWithValidCredentials() throws Exception {
    mockMvc()
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload("login-ok@example.com", "MANAGER")))
        .andExpect(status().isCreated());

    String loginPayload =
        """
        {"email": "login-ok@example.com", "password": "s3cret-password"}
        """;

    mockMvc()
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(not("")));
  }

  @Test
  void shouldRejectLoginWithWrongPassword() throws Exception {
    mockMvc()
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload("wrong-pwd@example.com", "CLIENT")))
        .andExpect(status().isCreated());

    String loginPayload =
        """
        {"email": "wrong-pwd@example.com", "password": "not-the-right-password"}
        """;

    mockMvc()
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginPayload))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldRejectLoginForUnknownEmail() throws Exception {
    String loginPayload =
        """
        {"email": "does-not-exist@example.com", "password": "whatever"}
        """;

    mockMvc()
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginPayload))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldRejectAccessToProtectedEndpointWithoutToken() throws Exception {
    mockMvc().perform(get("/reservations")).andExpect(status().isUnauthorized());
  }
}
