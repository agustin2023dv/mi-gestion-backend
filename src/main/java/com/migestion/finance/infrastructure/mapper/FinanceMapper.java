package com.migestion.finance.infrastructure.mapper;

import com.migestion.analytics.domain.SimuladorEscenario;
import com.migestion.finance.domain.AsignacionCostoIndirecto;
import com.migestion.finance.domain.CategoriaGasto;
import com.migestion.finance.domain.CentroCosto;
import com.migestion.finance.domain.ConfiguracionContabilidadCostos;
import com.migestion.finance.domain.CriterioProrrateo;
import com.migestion.finance.domain.GastoOperativo;
import com.migestion.finance.dto.AsignacionCostoIndirectoResponse;
import com.migestion.finance.dto.CategoriaGastoRequest;
import com.migestion.finance.dto.CategoriaGastoResponse;
import com.migestion.finance.dto.CentroCostoRequest;
import com.migestion.finance.dto.CentroCostoResponse;
import com.migestion.finance.dto.ConfiguracionContabilidadCostosResponse;
import com.migestion.finance.dto.CreateGastoOperativoRequest;
import com.migestion.finance.dto.CriterioProrrateoRequest;
import com.migestion.finance.dto.CriterioProrrateoResponse;
import com.migestion.finance.dto.GastoOperativoResponse;
import com.migestion.finance.dto.SimuladorEscenarioResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FinanceMapper {

    AsignacionCostoIndirectoResponse toAsignacionCostoIndirectoResponse(AsignacionCostoIndirecto asignacionCostoIndirecto);

    CentroCosto toCentroCosto(CentroCostoRequest request);

    CentroCostoResponse toCentroCostoResponse(CentroCosto centroCosto);

    CategoriaGasto toCategoriaGasto(CategoriaGastoRequest request);

    CategoriaGastoResponse toCategoriaGastoResponse(CategoriaGasto categoriaGasto);

    CriterioProrrateo toCriterioProrrateo(CriterioProrrateoRequest request);

    CriterioProrrateoResponse toCriterioProrrateoResponse(CriterioProrrateo criterioProrrateo);

    ConfiguracionContabilidadCostosResponse toConfiguracionContabilidadCostosResponse(
            ConfiguracionContabilidadCostos configuracionContabilidadCostos);

    GastoOperativo toGastoOperativo(CreateGastoOperativoRequest request);

    GastoOperativoResponse toGastoOperativoResponse(GastoOperativo gastoOperativo);

    @Mapping(target = "isSaved", source = "saved")
    SimuladorEscenarioResponse toSimuladorEscenarioResponse(SimuladorEscenario simuladorEscenario);
}
