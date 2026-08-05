package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
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
                auth.requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers("/ping")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/projections/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/movies/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.PUT, "/movies/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.POST, "/movies/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.PUT, "/projections/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.POST, "/projections/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.GET, "/rooms/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.PUT, "/rooms/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.POST, "/rooms/**")
                    .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.GET, "/reservations")
                    .hasAnyRole("MANAGER", "EMPLOYEE")
                    .requestMatchers(HttpMethod.GET, "/reservations/*")
                    .authenticated()
                    .requestMatchers(HttpMethod.PUT, "/reservations/**")
                    .hasAnyRole("MANAGER", "EMPLOYEE")
                    .requestMatchers(HttpMethod.POST, "/reservations/**")
                    .hasAnyRole("MANAGER", "EMPLOYEE")
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
