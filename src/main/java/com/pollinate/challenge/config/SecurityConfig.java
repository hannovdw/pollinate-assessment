package com.pollinate.challenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        // Disable CSRF for local development/testing with H2 and Postman
        .csrf(AbstractHttpConfigurer::disable)

        // Required to allow the H2 Console to load in a browser frame
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

        .authorizeHttpRequests(auth -> auth
            // 1. Permit Swagger UI and OpenAPI Docs
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()

            // Permit H2 Console for verification
            .requestMatchers("/h2-console/**").permitAll()

            // Allow Actuator health and info endpoints
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()

            // All other endpoints require authentication
            .anyRequest().authenticated()
        )
        // Use Basic Auth
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }
}

