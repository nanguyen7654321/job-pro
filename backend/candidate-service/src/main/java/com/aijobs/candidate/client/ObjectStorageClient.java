package com.aijobs.candidate.client;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ObjectStorageClient {
  // Local development targets MinIO, which is S3-compatible. Returning an S3-like
  // URL now makes it easy to swap this placeholder for a real MinIO/S3 SDK later.
  public String createResumeUrl(UUID candidateId, String originalFileName) {
    return "s3://resumes/" + candidateId + "/" + originalFileName;
  }
}
