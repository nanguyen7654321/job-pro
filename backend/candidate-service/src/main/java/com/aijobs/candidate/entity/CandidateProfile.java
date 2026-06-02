package com.aijobs.candidate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {
  @Id
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  private String headline;
  private String location;

  @Column(name = "total_experience_years")
  private Integer totalExperienceYears;

  @Column(name = "current_title")
  private String currentTitle;

  @Column(name = "desired_title")
  private String desiredTitle;

  @Column(name = "preferred_location")
  private String preferredLocation;

  @Column(name = "open_to_remote")
  private boolean openToRemote;

  @Column(name = "ai_summary")
  private String aiSummary;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  protected CandidateProfile() {}

  public CandidateProfile(UUID userId, String fullName) {
    this.id = UUID.randomUUID();
    this.userId = userId;
    this.fullName = fullName;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getFullName() {
    return fullName;
  }

  public String getHeadline() {
    return headline;
  }

  public String getLocation() {
    return location;
  }

  public Integer getTotalExperienceYears() {
    return totalExperienceYears;
  }

  public String getCurrentTitle() {
    return currentTitle;
  }

  public String getDesiredTitle() {
    return desiredTitle;
  }

  public String getPreferredLocation() {
    return preferredLocation;
  }

  public boolean isOpenToRemote() {
    return openToRemote;
  }

  public String getAiSummary() {
    return aiSummary;
  }

  public void updateProfile(
      String headline,
      String location,
      Integer totalExperienceYears,
      String currentTitle,
      String desiredTitle,
      String preferredLocation,
      boolean openToRemote,
      String aiSummary
  ) {
    this.headline = headline;
    this.location = location;
    this.totalExperienceYears = totalExperienceYears;
    this.currentTitle = currentTitle;
    this.desiredTitle = desiredTitle;
    this.preferredLocation = preferredLocation;
    this.openToRemote = openToRemote;
    this.aiSummary = aiSummary;
    this.updatedAt = Instant.now();
  }
}

