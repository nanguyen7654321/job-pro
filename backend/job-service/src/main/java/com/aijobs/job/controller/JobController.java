package com.aijobs.job.controller;

import com.aijobs.common.dto.ApiResponse;
import com.aijobs.job.dto.JobRequest;
import com.aijobs.job.dto.JobResponse;
import com.aijobs.job.service.JobService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employers/jobs")
public class JobController {
  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @PostMapping
  public ApiResponse<JobResponse> create(@Valid @RequestBody JobRequest request) {
    return ApiResponse.ok(jobService.create(request));
  }

  @GetMapping
  public ApiResponse<List<JobResponse>> list() {
    return ApiResponse.ok(jobService.list());
  }

  @GetMapping("/{jobId}")
  public ApiResponse<JobResponse> get(@PathVariable UUID jobId) {
    return ApiResponse.ok(new JobResponse(jobId, null, "Demo Job", "", null, null, null, null, null, null, "PUBLISHED"));
  }

  @PutMapping("/{jobId}")
  public ApiResponse<JobResponse> update(@PathVariable UUID jobId, @Valid @RequestBody JobRequest request) {
    return ApiResponse.ok(jobService.create(request));
  }

  @DeleteMapping("/{jobId}")
  public ApiResponse<Void> delete(@PathVariable UUID jobId) {
    return ApiResponse.ok(null);
  }
}

