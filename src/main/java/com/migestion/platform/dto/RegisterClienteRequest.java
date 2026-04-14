package com.migestion.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RegisterClienteRequest {

  @NotBlank(message = "Nombre is required")
  @Size(max = 100, message = "Nombre must be at most 100 characters")
  private String nombre;

  @NotBlank(message = "Apellido is required")
  @Size(max = 100, message = "Apellido must be at most 100 characters")
  private String apellido;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 255, message = "Email must be at most 255 characters")
  private String email;

  @Size(max = 50, message = "Telefono must be at most 50 characters")
  private String telefono;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
      message = "Password must contain at least one letter and one digit"
  )
  private String password;

  private Long tenantId;
}
