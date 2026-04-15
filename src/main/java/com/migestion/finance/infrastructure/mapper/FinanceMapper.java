package com.migestion.finance.infrastructure.mapper;

import com.migestion.finance.domain.CategoriaGasto;
import com.migestion.finance.domain.CentroCosto;
import com.migestion.finance.domain.CriterioProrrateo;
import com.migestion.finance.dto.CategoriaGastoRequest;
import com.migestion.finance.dto.CategoriaGastoResponse;
import com.migestion.finance.dto.CentroCostoRequest;
import com.migestion.finance.dto.CentroCostoResponse;
import com.migestion.finance.dto.CriterioProrrateoRequest;
import com.migestion.finance.dto.CriterioProrrateoResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface FinanceMapper {

    CentroCosto toCentroCosto(CentroCostoRequest request);

    CentroCostoResponse toCentroCostoResponse(CentroCosto centroCosto);

    CategoriaGasto toCategoriaGasto(CategoriaGastoRequest request);

    CategoriaGastoResponse toCategoriaGastoResponse(CategoriaGasto categoriaGasto);

    CriterioProrrateo toCriterioProrrateo(CriterioProrrateoRequest request);

    CriterioProrrateoResponse toCriterioProrrateoResponse(CriterioProrrateo criterioProrrateo);
}
