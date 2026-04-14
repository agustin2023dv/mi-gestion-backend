package com.migestion.shared.dto;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

  private boolean success;
  private T data;
  private Object error;
  private Instant timestamp;

  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .error(null)
        .timestamp(Instant.now())
        .build();
  }

  public static ApiResponse<Void> success() {
    return ApiResponse.<Void>builder()
        .success(true)
        .data(null)
        .error(null)
        .timestamp(Instant.now())
        .build();
  }
}