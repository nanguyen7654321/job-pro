package com.aijobs.job.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public record JobRequest(
    UUID companyId,
    @NotBlank String title,
    @NotBlank String description,
    String location,
    String employmentType,
    Integer experienceMin,
    Integer experienceMax,
    BigDecimal salaryMin,
    BigDecimal salaryMax
) {}

