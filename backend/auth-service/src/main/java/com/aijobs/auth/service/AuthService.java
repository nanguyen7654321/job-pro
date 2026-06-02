package com.aijobs.auth.service;

import com.aijobs.auth.dto.AuthRequest;
import com.aijobs.auth.dto.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  // MVP token generation is deliberately fake so the rest of the project can be
  // wired without choosing JWT signing details too early. Replace with password
  // hashing, user persistence, refresh tokens, and signed JWTs before launch.
  public AuthResponse issueToken(AuthRequest request) {
    var token = "mvp-token-for-" + request.email();
    return new AuthResponse(token, "Bearer", request.role());
  }
}
