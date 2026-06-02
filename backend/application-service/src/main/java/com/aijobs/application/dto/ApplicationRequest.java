package com.aijobs.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ApplicationRequest(
    @NotNull UUID jobId,
    @NotNull UUID candidateId
) {}

