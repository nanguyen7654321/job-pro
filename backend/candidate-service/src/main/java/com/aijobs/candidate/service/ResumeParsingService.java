package com.aijobs.candidate.service;

import com.aijobs.candidate.client.AiClient;
import org.springframework.stereotype.Service;

@Service
public class ResumeParsingService {
  private final AiClient aiClient;

  public ResumeParsingService(AiClient aiClient) {
    this.aiClient = aiClient;
  }

  public String parseResumeText(String resumeText) {
    return aiClient.parseResume(resumeText);
  }
}

