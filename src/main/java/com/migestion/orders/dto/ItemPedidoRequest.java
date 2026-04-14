package com.migestion.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record ItemPedidoRequest(
        @NotNull @Positive Long productoId,
        @NotNull @Positive Integer cantidad,
        List<@Valid IngredienteExtraRequest> ingredientesExtras
) {

    @Builder
    public record IngredienteExtraRequest(
            @NotNull @Positive Long ingredienteId,
            @NotNull @Positive BigDecimal cantidad
    ) {
    }
}