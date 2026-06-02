package com.aijobs.application.controller;

import com.aijobs.application.dto.ApplicationRequest;
import com.aijobs.application.dto.ApplicationResponse;
import com.aijobs.application.service.ApplicationWorkflowService;
import com.aijobs.common.dto.ApiResponse;
import com.aijobs.common.model.ApplicationStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApplicationController {
  private final ApplicationWorkflowService workflowService;

  public ApplicationController(ApplicationWorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  @PostMapping("/candidates/jobs/{jobId}/apply")
  public ApiResponse<ApplicationResponse> apply(
      @PathVariable UUID jobId,
      @Valid @RequestBody ApplicationRequest request
  ) {
    return ApiResponse.ok(workflowService.apply(new ApplicationRequest(jobId, request.candidateId())));
  }

  @GetMapping("/employers/jobs/{jobId}/applicants")
  public ApiResponse<List<ApplicationResponse>> applicants(@PathVariable UUID jobId) {
    return ApiResponse.ok(List.of());
  }

  @PutMapping("/employers/applications/{applicationId}/status")
  public ApiResponse<ApplicationResponse> updateStatus(
      @PathVariable UUID applicationId,
      @RequestParam ApplicationStatus status
  ) {
    return ApiResponse.ok(workflowService.updateStatus(applicationId, status));
  }
}

