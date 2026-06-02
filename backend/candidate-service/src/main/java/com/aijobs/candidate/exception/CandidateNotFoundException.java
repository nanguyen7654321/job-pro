package com.aijobs.candidate.exception;

import java.util.UUID;

public class CandidateNotFoundException extends RuntimeException {
  public CandidateNotFoundException(UUID id) {
    super("Candidate profile not found: " + id);
  }
}

