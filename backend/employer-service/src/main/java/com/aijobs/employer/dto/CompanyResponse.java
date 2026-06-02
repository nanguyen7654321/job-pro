package com.aijobs.employer.dto;

import java.util.UUID;

public record CompanyResponse(
    UUID id,
    String name,
    String website,
    String industry,
    String size
) {}

