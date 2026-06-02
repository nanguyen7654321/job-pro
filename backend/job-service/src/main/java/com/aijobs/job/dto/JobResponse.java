package com.aijobs.job.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record JobResponse(
    UUID id,
    UUID companyId,
    String title,
    String description,
    String location,
    String employmentType,
    Integer experienceMin,
    Integer experienceMax,
    BigDecimal salaryMin,
    BigDecimal salaryMax,
    String status
) {}

