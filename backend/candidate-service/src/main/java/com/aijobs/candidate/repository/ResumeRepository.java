package com.aijobs.candidate.repository;

import com.aijobs.candidate.entity.ResumeDocument;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<ResumeDocument, UUID> {
  Optional<ResumeDocument> findFirstByCandidateIdOrderByCreatedAtDesc(UUID candidateId);
}

