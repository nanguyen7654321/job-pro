package com.aijobs.matching.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MatchExplainRequest(
    @NotNull UUID candidateId,
    @NotNull UUID jobId,
    @Min(0) @Max(100) int skillsScore,
    @Min(0) @Max(100) int experienceScore,
    @Min(0) @Max(100) int titleScore,
    @Min(0) @Max(100) int locationScore,
    @Min(0) @Max(100) int aiReasoningScore
) {}

