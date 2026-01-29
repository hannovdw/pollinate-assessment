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
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // Disable CSRF for local development/testing with H2 and Postman
        .csrf(AbstractHttpConfigurer::disable)

        // Required to allow the H2 Console to load in a browser frame
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

        .authorizeHttpRequests(auth -> auth
            // 1. Permit Swagger UI and OpenAPI Docs (Requirement 7)
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()

            // 2. Permit H2 Console for verification (Requirement 2)
            .requestMatchers("/h2-console/**").permitAll()

            // Allow Actuator health and info endpoints
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()

            // 3. All other endpoints require authentication (Requirement 5)
            .anyRequest().authenticated()
        )
        // Use Basic Auth (Requirement 5)
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }
}

