package com.aijobs.candidate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CandidateProfileRequest(
    @NotNull UUID userId,
    @NotBlank String fullName,
    String headline,
    String location,
    Integer totalExperienceYears,
    String currentTitle,
    String desiredTitle,
    String preferredLocation,
    boolean openToRemote
) {}

