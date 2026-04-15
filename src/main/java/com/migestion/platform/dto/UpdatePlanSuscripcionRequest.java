package com.migestion.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePlanSuscripcionRequest {

    @Size(max = 100, message = "Nombre must be at most 100 characters")
    private String nombre;

    @PositiveOrZero(message = "Max productos must be zero or positive")
    private Integer maxProductos;

    @PositiveOrZero(message = "Max pedidos mensuales must be zero or positive")
    private Integer maxPedidosMensuales;

    @PositiveOrZero(message = "Max almacenamiento mb must be zero or positive")
    private Integer maxAlmacenamientoMb;

    @DecimalMin(value = "0.00", inclusive = true, message = "Precio mensual must be zero or positive")
    private BigDecimal precioMensual;

    private List<@Size(max = 100, message = "Each feature must be at most 100 characters") String> features;

    @PositiveOrZero(message = "Order level must be zero or positive")
    private Integer orderLevel;
}
