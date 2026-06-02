package com.aijobs.candidate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resumes")
public class ResumeDocument {
  @Id
  private UUID id;

  @Column(name = "candidate_id", nullable = false)
  private UUID candidateId;

  @Column(name = "file_url", nullable = false)
  private String fileUrl;

  @Column(name = "parsed_json")
  private String parsedJson;

  @Column(name = "ai_summary")
  private String aiSummary;

  @Column(name = "embedding_id")
  private UUID embeddingId;

  @Column(name = "created_at")
  private Instant createdAt;

  protected ResumeDocument() {}

  public ResumeDocument(UUID candidateId, String fileUrl) {
    this.id = UUID.randomUUID();
    this.candidateId = candidateId;
    this.fileUrl = fileUrl;
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getCandidateId() {
    return candidateId;
  }

  public String getFileUrl() {
    return fileUrl;
  }

  public String getParsedJson() {
    return parsedJson;
  }

  public String getAiSummary() {
    return aiSummary;
  }

  public void applyAiParse(String parsedJson, String aiSummary, UUID embeddingId) {
    this.parsedJson = parsedJson;
    this.aiSummary = aiSummary;
    this.embeddingId = embeddingId;
  }
}

