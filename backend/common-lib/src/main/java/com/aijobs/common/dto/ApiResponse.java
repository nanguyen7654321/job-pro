package com.aijobs.common.dto;

import java.time.Instant;

// Shared envelope for successful responses. A consistent shape makes frontend
// API clients simpler and leaves room for request IDs or pagination metadata.
public record ApiResponse<T>(
    T data,
    String message,
    Instant timestamp
) {
  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(data, "ok", Instant.now());
  }
}
