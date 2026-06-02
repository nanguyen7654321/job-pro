package com.aijobs.candidate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aijobs.candidate")
public record CandidateServiceProperties(
    int maxResumeSizeMb
) {
  public CandidateServiceProperties {
    if (maxResumeSizeMb <= 0) {
      maxResumeSizeMb = 10;
    }
  }
}

