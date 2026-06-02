package com.aijobs.candidate.repository;

import com.aijobs.candidate.entity.CandidateProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<CandidateProfile, UUID> {
  Optional<CandidateProfile> findByUserId(UUID userId);
}

