package com.aijobs.candidate.service;

import com.aijobs.candidate.client.AiClient;
import com.aijobs.candidate.dto.CandidateProfileRequest;
import com.aijobs.candidate.dto.CandidateProfileResponse;
import com.aijobs.candidate.entity.CandidateProfile;
import com.aijobs.candidate.exception.CandidateNotFoundException;
import com.aijobs.candidate.repository.CandidateRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidateProfileService {
  private final CandidateRepository repository;
  private final AiClient aiClient;

  public CandidateProfileService(CandidateRepository repository, AiClient aiClient) {
    this.repository = repository;
    this.aiClient = aiClient;
  }

  // Upsert keeps onboarding forgiving: a candidate can save a profile early and
  // refine it after resume parsing. The generated summary is stored so matching
  // and UI reads do not need to call the LLM on every request.
  @Transactional
  public CandidateProfileResponse createOrUpdate(CandidateProfileRequest request) {
    var profile = repository.findByUserId(request.userId())
        .orElseGet(() -> new CandidateProfile(request.userId(), request.fullName()));
    var summary = aiClient.summarizeProfile(
        request.fullName(),
        request.currentTitle(),
        request.totalExperienceYears()
    );
    profile.updateProfile(
        request.headline(),
        request.location(),
        request.totalExperienceYears(),
        request.currentTitle(),
        request.desiredTitle(),
        request.preferredLocation(),
        request.openToRemote(),
        summary
    );
    return toResponse(repository.save(profile));
  }

  @Transactional(readOnly = true)
  public CandidateProfileResponse getByUserId(UUID userId) {
    return repository.findByUserId(userId)
        .map(this::toResponse)
        .orElseThrow(() -> new CandidateNotFoundException(userId));
  }

  private CandidateProfileResponse toResponse(CandidateProfile profile) {
    return new CandidateProfileResponse(
        profile.getId(),
        profile.getUserId(),
        profile.getFullName(),
        profile.getHeadline(),
        profile.getLocation(),
        profile.getTotalExperienceYears(),
        profile.getCurrentTitle(),
        profile.getDesiredTitle(),
        profile.getPreferredLocation(),
        profile.isOpenToRemote(),
        profile.getAiSummary()
    );
  }
}
