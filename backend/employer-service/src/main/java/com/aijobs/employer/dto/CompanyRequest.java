package com.aijobs.employer.dto;

import jakarta.validation.constraints.NotBlank;

public record CompanyRequest(
    @NotBlank String name,
    String website,
    String industry,
    String size
) {}

