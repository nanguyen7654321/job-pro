package com.aijobs.matching.controller;

import com.aijobs.common.dto.ApiResponse;
import com.aijobs.matching.dto.MatchExplainRequest;
import com.aijobs.matching.dto.MatchScoreResponse;
import com.aijobs.matching.service.MatchingService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {
  private final MatchingService matchingService;

  public MatchingController(MatchingService matchingService) {
    this.matchingService = matchingService;
  }

  // Refresh endpoints separate expensive embedding work from read paths. They
  // can become async jobs once queueing is introduced.
  @PostMapping("/candidate/{candidateId}/refresh")
  public ApiResponse<String> refreshCandidate(@PathVariable UUID candidateId) {
    return ApiResponse.ok("candidate embedding refresh queued: " + candidateId);
  }

  @PostMapping("/job/{jobId}/refresh")
  public ApiResponse<String> refreshJob(@PathVariable UUID jobId) {
    return ApiResponse.ok("job embedding refresh queued: " + jobId);
  }

  @GetMapping("/candidate/{candidateId}/jobs")
  public ApiResponse<List<MatchScoreResponse>> jobsForCandidate(@PathVariable UUID candidateId) {
    return ApiResponse.ok(List.of());
  }

  @GetMapping("/job/{jobId}/candidates")
  public ApiResponse<List<MatchScoreResponse>> candidatesForJob(@PathVariable UUID jobId) {
    return ApiResponse.ok(List.of());
  }

  @PostMapping("/explain")
  public ApiResponse<MatchScoreResponse> explain(@Valid @RequestBody MatchExplainRequest request) {
    return ApiResponse.ok(matchingService.explain(request));
  }
}
