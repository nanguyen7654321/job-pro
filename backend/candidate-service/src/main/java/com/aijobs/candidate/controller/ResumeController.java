package com.aijobs.candidate.controller;

import com.aijobs.candidate.client.ObjectStorageClient;
import com.aijobs.candidate.dto.ResumeUploadResponse;
import com.aijobs.candidate.entity.ResumeDocument;
import com.aijobs.candidate.repository.ResumeRepository;
import com.aijobs.candidate.service.CandidateEmbeddingService;
import com.aijobs.candidate.service.ResumeParsingService;
import com.aijobs.common.dto.ApiResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/candidates/resume")
public class ResumeController {
  private final ResumeRepository resumeRepository;
  private final ObjectStorageClient objectStorageClient;
  private final ResumeParsingService parsingService;
  private final CandidateEmbeddingService embeddingService;

  public ResumeController(
      ResumeRepository resumeRepository,
      ObjectStorageClient objectStorageClient,
      ResumeParsingService parsingService,
      CandidateEmbeddingService embeddingService
  ) {
    this.resumeRepository = resumeRepository;
    this.objectStorageClient = objectStorageClient;
    this.parsingService = parsingService;
    this.embeddingService = embeddingService;
  }

  // The scaffold accepts extracted text as a request parameter so teams can test
  // the AI parse/persist flow before adding binary upload and text extraction.
  // Production should accept multipart files, scan them, store them in object
  // storage, and then run extraction asynchronously.
  @PostMapping("/upload")
  public ApiResponse<ResumeUploadResponse> upload(
      @RequestHeader("X-Candidate-Id") UUID candidateId,
      @RequestParam String fileName,
      @RequestParam(defaultValue = "") String extractedText
  ) {
    var fileUrl = objectStorageClient.createResumeUrl(candidateId, fileName);
    var resume = new ResumeDocument(candidateId, fileUrl);
    var parsedJson = parsingService.parseResumeText(extractedText);
    var embeddingId = embeddingService.refreshCandidateEmbedding(candidateId, parsedJson);
    resume.applyAiParse(parsedJson, "AI resume summary pending", embeddingId);
    var saved = resumeRepository.save(resume);
    return ApiResponse.ok(new ResumeUploadResponse(
        saved.getId(),
        saved.getCandidateId(),
        saved.getFileUrl(),
        saved.getAiSummary()
    ));
  }

  @GetMapping("/latest")
  public ApiResponse<ResumeUploadResponse> latest(
      @RequestHeader("X-Candidate-Id") UUID candidateId
  ) {
    var resume = resumeRepository.findFirstByCandidateIdOrderByCreatedAtDesc(candidateId)
        .orElseThrow();
    return ApiResponse.ok(new ResumeUploadResponse(
        resume.getId(),
        resume.getCandidateId(),
        resume.getFileUrl(),
        resume.getAiSummary()
    ));
  }
}
