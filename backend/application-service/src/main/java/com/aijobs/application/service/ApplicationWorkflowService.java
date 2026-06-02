package com.aijobs.application.service;

import com.aijobs.application.dto.ApplicationRequest;
import com.aijobs.application.dto.ApplicationResponse;
import com.aijobs.common.model.ApplicationStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ApplicationWorkflowService {
  // Application creation should eventually trigger match scoring and notification
  // workflows. The scaffold returns APPLIED immediately so the API contract can
  // be exercised from the UI first.
  public ApplicationResponse apply(ApplicationRequest request) {
    return new ApplicationResponse(
        UUID.randomUUID(),
        request.jobId(),
        request.candidateId(),
        ApplicationStatus.APPLIED,
        BigDecimal.ZERO,
        "AI review pending"
    );
  }

  public ApplicationResponse updateStatus(UUID applicationId, ApplicationStatus status) {
    return new ApplicationResponse(applicationId, null, null, status, null, null);
  }
}
