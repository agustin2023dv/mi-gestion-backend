package com.migestion.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateTenantRequest {

    @NotBlank(message = "Nombre del negocio is required")
    @Size(max = 150, message = "Nombre del negocio must be at most 150 characters")
    private String nombreNegocio;

    @NotBlank(message = "Slug is required")
    @Size(max = 150, message = "Slug must be at most 150 characters")
    @Pattern(
            regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "Slug must contain only lowercase letters, numbers, and hyphens"
    )
    private String slug;

    @NotNull(message = "Plan suscripcion id is required")
    @Positive(message = "Plan suscripcion id must be positive")
    private Long planSuscripcionId;

    @NotBlank(message = "Propietario nombre is required")
    @Size(max = 100, message = "Propietario nombre must be at most 100 characters")
    private String propietarioNombre;

    @NotBlank(message = "Propietario apellido is required")
    @Size(max = 100, message = "Propietario apellido must be at most 100 characters")
    private String propietarioApellido;

    @NotBlank(message = "Propietario email is required")
    @Email(message = "Propietario email must be valid")
    @Size(max = 255, message = "Propietario email must be at most 255 characters")
    private String propietarioEmail;

    @Size(max = 50, message = "Propietario telefono must be at most 50 characters")
    private String propietarioTelefono;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
        message = "Password must contain at least one letter and one digit"
    )
    private String password;
}
