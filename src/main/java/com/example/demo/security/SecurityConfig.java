package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth
                    // Authentication endpoints are open to everyone.
                    .requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers("/ping")
                    .permitAll()

                    // GET /projections: should return 200 for everyone.
                    .requestMatchers(HttpMethod.GET, "/projections/**")
                    .permitAll()

                    // GET /movies: open read access (not restricted by the spec).
                    .requestMatchers(HttpMethod.GET, "/movies/**")
                    .permitAll()

                    // PUT /movies: 403 for CLIENTS and EMPLOYEES, 200 for MANAGERS.
                    .requestMatchers(HttpMethod.PUT, "/movies/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.POST, "/movies/**")
                    .hasRole("MANAGER")

                    // PUT /projection: 403 for CLIENT and EMPLOYEES, 200 for MANAGERS.
                    .requestMatchers(HttpMethod.PUT, "/projections/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.POST, "/projections/**")
                    .hasRole("MANAGER")

                    // Rooms: not covered by the spec's EXAMPLES; kept consistent with movies
                    // (write restricted to MANAGER, read open) until told otherwise.
                    .requestMatchers(HttpMethod.GET, "/rooms/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.PUT, "/rooms/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.POST, "/rooms/**")
                    .hasRole("MANAGER")

                    // GET /reservations (list all): 403 for CLIENTS, 200 for MANAGERS/EMPLOYEES.
                    .requestMatchers(HttpMethod.GET, "/reservations")
                    .hasAnyRole("MANAGER", "EMPLOYEE")

                    // GET /reservationById: authenticated; ownership for CLIENTS is enforced
                    // in the controller (AuthorizationRules.canViewReservation), since it
                    // depends on the resource's owner, not just the role.
                    .requestMatchers(HttpMethod.GET, "/reservations/*")
                    .authenticated()

                    // PUT /reservation: 403 for CLIENTS, 200 for EMPLOYEES and MANAGERS.
                    .requestMatchers(HttpMethod.PUT, "/reservations/**")
                    .hasAnyRole("MANAGER", "EMPLOYEE")
                    .requestMatchers(HttpMethod.POST, "/reservations/**")
                    .hasAnyRole("MANAGER", "EMPLOYEE")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
