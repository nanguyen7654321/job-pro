package com.aijobs.candidate.dto;

import java.util.UUID;

public record ResumeUploadResponse(
    UUID resumeId,
    UUID candidateId,
    String fileUrl,
    String aiSummary
) {}

