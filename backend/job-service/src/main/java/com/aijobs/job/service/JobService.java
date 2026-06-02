package com.aijobs.job.service;

import com.aijobs.job.dto.JobRequest;
import com.aijobs.job.dto.JobResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JobService {
  // Job creation will later parse descriptions, extract required skills, and
  // generate embeddings. Keeping that behind this service avoids putting AI
  // workflow decisions in the controller.
  public JobResponse create(JobRequest request) {
    return new JobResponse(
        UUID.randomUUID(),
        request.companyId(),
        request.title(),
        request.description(),
        request.location(),
        request.employmentType(),
        request.experienceMin(),
        request.experienceMax(),
        request.salaryMin(),
        request.salaryMax(),
        "DRAFT"
    );
  }

  public List<JobResponse> list() {
    return List.of();
  }
}
