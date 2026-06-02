package com.aijobs.auth.dto;

import com.aijobs.common.model.UserRole;

public record AuthResponse(
    String accessToken,
    String tokenType,
    UserRole role
) {}

