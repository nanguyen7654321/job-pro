package com.aijobs.employer.controller;

import com.aijobs.common.dto.ApiResponse;
import com.aijobs.employer.dto.CompanyRequest;
import com.aijobs.employer.dto.CompanyResponse;
import com.aijobs.employer.service.EmployerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {
  private final EmployerService employerService;

  public EmployerController(EmployerService employerService) {
    this.employerService = employerService;
  }

  @PostMapping("/company")
  public ApiResponse<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest request) {
    return ApiResponse.ok(employerService.createCompany(request));
  }

  @GetMapping("/company/me")
  public ApiResponse<CompanyResponse> myCompany() {
    return ApiResponse.ok(new CompanyResponse(null, "Demo Company", null, null, null));
  }
}

