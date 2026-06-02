package com.aijobs.common.error;

import java.time.Instant;
import java.util.List;

// Shared error contract. Use stable error codes so web apps can respond to
// validation, auth, and workflow errors without parsing human-readable text.
public record ErrorResponse(
    String code,
    String message,
    List<String> details,
    Instant timestamp
) {
  public static ErrorResponse of(String code, String message) {
    return new ErrorResponse(code, message, List.of(), Instant.now());
  }
}
