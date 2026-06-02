package com.aijobs.application.dto;

import com.aijobs.common.model.ApplicationStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record ApplicationResponse(
    UUID id,
    UUID jobId,
    UUID candidateId,
    ApplicationStatus status,
    BigDecimal matchScore,
    String aiSummary
) {}

