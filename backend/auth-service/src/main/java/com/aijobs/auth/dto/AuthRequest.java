package com.aijobs.auth.dto;

import com.aijobs.common.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
    @Email String email,
    @NotBlank String password,
    @NotNull UserRole role
) {}

