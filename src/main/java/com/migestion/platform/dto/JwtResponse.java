package com.migestion.platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

  @JsonProperty("user")
  private UserProfile user;

  /**
   * Personal data of the authenticated user, returned once at login.
   * Avoids the need for a separate /me call immediately after auth.
   */
  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class UserProfile {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("role")
    private String role;

    /** Null for SuperAdmin; the owning tenant's DB id for all other roles. */
    @JsonProperty("tenantId")
    private Long tenantId;

    /** Human-readable tenant slug, e.g. "pizzeria-del-sol". Null for SuperAdmin. */
    @JsonProperty("tenantSlug")
    private String tenantSlug;

    /** Display name of the tenant business. Null for SuperAdmin. */
    @JsonProperty("tenantNombre")
    private String tenantNombre;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("isActive")
    private Boolean isActive;
  }
}
