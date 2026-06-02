package com.aijobs.gateway.controller;

import com.aijobs.common.dto.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gateway")
public class GatewayHealthController {
  // Exposes the intended route ownership while the MVP gateway is still a Spring
  // Boot module. Later this can be replaced by Spring Cloud Gateway or a managed
  // API gateway route table.
  @GetMapping("/routes")
  public ApiResponse<Map<String, String>> routes() {
    return ApiResponse.ok(Map.of(
        "auth", "/api/auth/**",
        "candidates", "/api/candidates/**",
        "employers", "/api/employers/**",
        "jobs", "/api/jobs/**",
        "matching", "/api/matching/**"
    ));
  }
}
