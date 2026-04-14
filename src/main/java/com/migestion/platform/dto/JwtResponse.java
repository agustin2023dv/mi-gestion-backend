package com.migestion.platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JwtResponse {

  @JsonProperty("accessToken")
  private String accessToken;

  @JsonProperty("refreshToken")
  private String refreshToken;

  @JsonProperty("tokenType")
  @Builder.Default
  private String tokenType = "Bearer";

  @JsonProperty("expiresIn")
  private long expiresIn;
}
