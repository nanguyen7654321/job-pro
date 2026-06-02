package com.aijobs.matching.dto;

import java.util.List;
import java.util.UUID;

public record MatchScoreResponse(
    UUID candidateId,
    UUID jobId,
    double overallScore,
    int skillsScore,
    int experienceScore,
    int titleScore,
    int locationScore,
    int aiReasoningScore,
    String explanation,
    List<String> missingSkills
) {}

