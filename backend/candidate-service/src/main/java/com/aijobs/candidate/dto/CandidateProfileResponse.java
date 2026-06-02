package com.aijobs.candidate.dto;

import java.util.UUID;

public record CandidateProfileResponse(
    UUID id,
    UUID userId,
    String fullName,
    String headline,
    String location,
    Integer totalExperienceYears,
    String currentTitle,
    String desiredTitle,
    String preferredLocation,
    boolean openToRemote,
    String aiSummary
) {}

