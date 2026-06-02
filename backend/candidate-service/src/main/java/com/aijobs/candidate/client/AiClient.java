package com.aijobs.candidate.client;

import org.springframework.stereotype.Component;

@Component
public class AiClient {
  // This placeholder protects the domain services from provider churn. Replace
  // the implementation with OpenAI or Vertex AI calls without changing
  // controllers or persistence code.
  public String summarizeProfile(String fullName, String currentTitle, Integer years) {
    var title = currentTitle == null || currentTitle.isBlank() ? "candidate" : currentTitle;
    var experience = years == null ? "unknown" : years.toString();
    return fullName + " is a " + title + " with " + experience + " years of experience.";
  }

  // Resume parsing should use ai/prompts/resume-parser.prompt.md and return
  // strict JSON. The scaffold returns deterministic JSON for local development.
  public String parseResume(String resumeText) {
    return "{\"summary\":\"Parsed resume placeholder\",\"skills\":[]}";
  }
}
