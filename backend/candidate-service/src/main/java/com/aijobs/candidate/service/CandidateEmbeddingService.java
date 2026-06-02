package com.aijobs.candidate.service;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CandidateEmbeddingService {
  // This deterministic ID stands in for a real embedding row. Production should
  // call an embedding model, store the vector in pgvector, and return that row ID.
  public UUID refreshCandidateEmbedding(UUID candidateId, String normalizedProfile) {
    return UUID.nameUUIDFromBytes((candidateId + ":" + normalizedProfile).getBytes());
  }
}
