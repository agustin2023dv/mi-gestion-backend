package com.migestion.hr.application;

import com.migestion.hr.domain.Empleado;
import com.migestion.hr.domain.EmpleadoRepository;
import com.migestion.hr.domain.NominaEmpleado;
import com.migestion.hr.domain.NominaEmpleadoRepository;
import com.migestion.hr.dto.CalculatePayrollRequest;
import com.migestion.hr.dto.NominaEmpleadoResponse;
import com.migestion.hr.infrastructure.mapper.HrMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalculatePayrollUseCase {

    private final EmpleadoRepository empleadoRepository;
    private final NominaEmpleadoRepository nominaEmpleadoRepository;
    private final HrMapper hrMapper;

    public CalculatePayrollUseCase(
            EmpleadoRepository empleadoRepository,
            NominaEmpleadoRepository nominaEmpleadoRepository,
            HrMapper hrMapper) {
        this.empleadoRepository = empleadoRepository;
        this.nominaEmpleadoRepository = nominaEmpleadoRepository;
        this.hrMapper = hrMapper;
    }

    @Transactional
    public NominaEmpleadoResponse execute(Long tenantId, CalculatePayrollRequest request) {
        Empleado empleado = empleadoRepository.findByIdAndTenantId(request.empleadoId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", request.empleadoId()));

        BigDecimal sueldoBase = calculateBaseSalary(empleado, request);
        BigDecimal comisiones = empleado.getPorcentajeComision() != null
                ? empleado.getPorcentajeComision()
                : BigDecimal.ZERO;
        BigDecimal bonificaciones = request.bonificaciones() != null ? request.bonificaciones() : BigDecimal.ZERO;
        BigDecimal descuentos = request.descuentos() != null ? request.descuentos() : BigDecimal.ZERO;
        BigDecimal total = sueldoBase.add(comisiones).add(bonificaciones).subtract(descuentos);

        NominaEmpleado nomina = NominaEmpleado.builder()
                .tenantId(tenantId)
                .empleadoId(empleado.getId())
                .periodoInicio(request.periodoInicio())
                .periodoFin(request.periodoFin())
                .diasTrabajados(request.diasTrabajados())
                .horasTrabajadas(request.horasTrabajadas())
                .sueldoBaseCalculado(sueldoBase)
                .comisionesGeneradas(comisiones)
                .bonificaciones(bonificaciones)
                .descuentos(descuentos)
                .totalAPagar(total)
                .build();

        NominaEmpleado saved = nominaEmpleadoRepository.save(nomina);
        return hrMapper.toResponse(saved);
    }

    private BigDecimal calculateBaseSalary(Empleado empleado, CalculatePayrollRequest request) {
        if ("fijo".equals(empleado.getTipoRemuneracion()) && empleado.getMontoSueldoFijo() != null) {
            return empleado.getMontoSueldoFijo();
        }
        if ("hora".equals(empleado.getTipoRemuneracion()) && empleado.getCostoHoraBase() != null
                && request.horasTrabajadas() != null) {
            return empleado.getCostoHoraBase().multiply(request.horasTrabajadas());
        }
        return BigDecimal.ZERO;
    }
}
