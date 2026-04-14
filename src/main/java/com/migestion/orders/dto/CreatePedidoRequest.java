package com.migestion.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record CreatePedidoRequest(
        @Valid ClienteInvitadoRequest cliente,
        @NotEmpty List<@Valid ItemPedidoRequest> items,
        @NotBlank @Size(max = 50) String tipoEntrega,
        @Positive Long direccionEntregaId,
        @Positive Long tarifaDeliveryId,
        @Size(max = 100) String cuponCodigo,
        Instant fechaEntregaSolicitada,
        @Size(max = 2000) String notasCliente
) {

    @Builder
    public record ClienteInvitadoRequest(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Size(max = 100) String apellido,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(max = 30) String telefono
    ) {
    }
}