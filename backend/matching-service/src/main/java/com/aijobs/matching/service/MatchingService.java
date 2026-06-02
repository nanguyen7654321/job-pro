package com.aijobs.matching.service;

import com.aijobs.matching.dto.MatchExplainRequest;
import com.aijobs.matching.dto.MatchScoreResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {
  // Keep scoring deterministic before invoking an LLM. This makes matching
  // explainable, testable, and debuggable; the LLM should explain or refine, not
  // secretly decide the whole score.
  public MatchScoreResponse explain(MatchExplainRequest request) {
    var overall = request.skillsScore() * 0.40
        + request.experienceScore() * 0.25
        + request.titleScore() * 0.15
        + request.locationScore() * 0.10
        + request.aiReasoningScore() * 0.10;

    return new MatchScoreResponse(
        request.candidateId(),
        request.jobId(),
        overall,
        request.skillsScore(),
        request.experienceScore(),
        request.titleScore(),
        request.locationScore(),
        request.aiReasoningScore(),
        "Weighted MVP match score generated from skills, experience, title, location, and AI reasoning.",
        List.of()
    );
  }
}
