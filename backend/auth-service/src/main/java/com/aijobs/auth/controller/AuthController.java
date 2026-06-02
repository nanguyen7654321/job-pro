package com.aijobs.auth.controller;

import com.aijobs.auth.dto.AuthRequest;
import com.aijobs.auth.dto.AuthResponse;
import com.aijobs.auth.service.AuthService;
import com.aijobs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public ApiResponse<AuthResponse> signup(@Valid @RequestBody AuthRequest request) {
    return ApiResponse.ok(authService.issueToken(request));
  }

  @PostMapping("/login")
  public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    return ApiResponse.ok(authService.issueToken(request));
  }
}

