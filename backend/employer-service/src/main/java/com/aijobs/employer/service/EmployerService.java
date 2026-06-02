package com.aijobs.employer.service;

import com.aijobs.employer.dto.CompanyRequest;
import com.aijobs.employer.dto.CompanyResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EmployerService {
  public CompanyResponse createCompany(CompanyRequest request) {
    return new CompanyResponse(
        UUID.randomUUID(),
        request.name(),
        request.website(),
        request.industry(),
        request.size()
    );
  }
}

