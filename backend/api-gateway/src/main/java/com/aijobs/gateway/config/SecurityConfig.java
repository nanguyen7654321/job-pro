package com.aijobs.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Route discovery and health are public for local development. Real gateway
    // JWT validation should be added before protected business routes are wired
    // through this module.
    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/gateway/routes",
                "/actuator/health",
                "/actuator/info",
                "/actuator/prometheus",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml"
            )
            .permitAll()
            .anyRequest().authenticated()
        )
        .build();
  }
}
