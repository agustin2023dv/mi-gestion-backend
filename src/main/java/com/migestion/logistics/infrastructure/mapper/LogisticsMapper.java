package com.migestion.logistics.infrastructure.mapper;

import com.migestion.logistics.domain.ComprobanteEntrega;
import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.FirmaDigital;
import com.migestion.logistics.domain.TarifaDelivery;
import com.migestion.logistics.dto.ComprobanteResponse;
import com.migestion.logistics.dto.EntregaResponse;
import com.migestion.logistics.dto.FirmaResponse;
import com.migestion.logistics.dto.TarifaDeliveryRequest;
import com.migestion.logistics.dto.TarifaDeliveryResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface LogisticsMapper {

    // ── TarifaDelivery ──────────────────────────────────────────────────────

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "tipoCalculo", source = "tipoCalculo")
    @Mapping(target = "precioBase", source = "precioBase")
    @Mapping(target = "precioPorKm", source = "precioPorKm")
    @Mapping(target = "distanciaMinimaKm", source = "distanciaMinimaKm")
    @Mapping(target = "distanciaMaximaKm", source = "distanciaMaximaKm")
    @Mapping(target = "active", source = "isActive")
    TarifaDelivery toEntity(TarifaDeliveryRequest request);

    @Mapping(target = "isActive", source = "active")
    TarifaDeliveryResponse toResponse(TarifaDelivery tarifaDelivery);

    // ── Entrega ──────────────────────────────────────────────────────────────
    // Nested pedido and repartidor are populated by the use case after
    // fetching from their respective bounded contexts.

    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "repartidor", ignore = true)
    EntregaResponse toResponse(Entrega entrega);

    // ── FirmaDigital ─────────────────────────────────────────────────────────
    // qrCodeUrl and expiracion are computed at generation time by the use case.

    @Mapping(target = "token", source = "tokenUnico")
    @Mapping(target = "qrCodeData", source = "qrCodeData")
    @Mapping(target = "qrCodeUrl", ignore = true)
    @Mapping(target = "expiracion", ignore = true)
    FirmaResponse toResponse(FirmaDigital firmaDigital);

    // ── ComprobanteEntrega ───────────────────────────────────────────────────

    ComprobanteResponse toResponse(ComprobanteEntrega comprobanteEntrega);
}
