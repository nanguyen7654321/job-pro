package com.aijobs.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Auth endpoints must stay public so users can sign up or log in before a
    // token exists. CSRF is disabled because this service exposes stateless JSON
    // APIs instead of browser form sessions.
    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/auth/**",
                "/actuator/health",
                "/actuator/info",
                "/actuator/prometheus"
            )
            .permitAll()
            .anyRequest().authenticated()
        )
        .build();
  }
}
