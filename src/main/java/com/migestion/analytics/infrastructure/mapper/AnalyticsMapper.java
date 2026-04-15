package com.migestion.analytics.infrastructure.mapper;

import com.migestion.analytics.domain.DashboardCosto;
import com.migestion.analytics.dto.CategoriaTopResponse;
import com.migestion.analytics.dto.DashboardCostoResponse;
import com.migestion.analytics.dto.DashboardVentaResponse;
import com.migestion.analytics.dto.ProductoTopResponse;
import com.migestion.analytics.dto.VentasGraficoResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AnalyticsMapper {

    DashboardVentaResponse toDashboardVentaResponse(String periodo, DashboardVentaResponse.Metricas metricas);

    DashboardCostoResponse toDashboardCostoResponse(String periodo, DashboardCostoResponse.Metricas metricas);

        default VentasGraficoResponse toVentasGraficoResponse(List<VentasGraficoResponse.Serie> series) {
                return VentasGraficoResponse.builder()
                                .series(series)
                                .build();
        }

    ProductoTopResponse toProductoTopResponse(ProductoTopProjection projection);

    List<ProductoTopResponse> toProductoTopResponses(List<ProductoTopProjection> projections);

    CategoriaTopResponse toCategoriaTopResponse(CategoriaTopProjection projection);

    List<CategoriaTopResponse> toCategoriaTopResponses(List<CategoriaTopProjection> projections);

    @Mapping(target = "costoIngredientes", source = "costo.costosIngredientesPeriodo")
    @Mapping(target = "costoEmpleados", source = "costo.costosEmpleadosPeriodo")
    @Mapping(target = "costoCIFAsignado", source = "costo.cogsCifAsignado")
    @Mapping(target = "gastosFijosTotales", source = "costo.gastosFijosTotales")
    DashboardCostoResponse.Metricas toDashboardCostoMetricas(
            DashboardCosto costo,
            BigDecimal ingresosTotales,
            BigDecimal costoTotal,
            BigDecimal margenPorcentual,
            BigDecimal margenNeto
    );

    record ProductoTopProjection(
            Long productoId,
            String nombre,
            Long cantidadVendida,
            BigDecimal ingresosGenerados
    ) {
    }

    record CategoriaTopProjection(
            Long categoriaId,
            String nombre,
            BigDecimal ingresosGenerados,
            BigDecimal porcentaje
    ) {
    }

    record SerieVentasProjection(
            LocalDate fecha,
            BigDecimal ingresos,
            Long pedidos
    ) {
    }
}