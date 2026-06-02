package com.aijobs.candidate.controller;

import com.aijobs.candidate.dto.CandidateProfileRequest;
import com.aijobs.candidate.dto.CandidateProfileResponse;
import com.aijobs.candidate.service.CandidateProfileService;
import com.aijobs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {
  private final CandidateProfileService profileService;

  public CandidateController(CandidateProfileService profileService) {
    this.profileService = profileService;
  }

  // Creates the canonical candidate profile used by matching. Resume parsing can
  // enrich the same profile later, but this endpoint lets candidates start with
  // explicit preferences before AI extraction is available.
  @PostMapping("/profile")
  public ApiResponse<CandidateProfileResponse> createProfile(
      @Valid @RequestBody CandidateProfileRequest request
  ) {
    return ApiResponse.ok(profileService.createOrUpdate(request));
  }

  // MVP identity is passed as a header to keep the scaffold simple. Replace this
  // with JWT subject extraction once Spring Security is fully wired.
  @GetMapping("/profile/me")
  public ApiResponse<CandidateProfileResponse> getMyProfile(
      @RequestHeader("X-User-Id") UUID userId
  ) {
    return ApiResponse.ok(profileService.getByUserId(userId));
  }

  @PutMapping("/profile/me")
  public ApiResponse<CandidateProfileResponse> updateMyProfile(
      @Valid @RequestBody CandidateProfileRequest request
  ) {
    return ApiResponse.ok(profileService.createOrUpdate(request));
  }
}
