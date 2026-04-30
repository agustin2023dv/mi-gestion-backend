package com.migestion.hr.application;

import com.migestion.hr.domain.Empleado;
import com.migestion.hr.domain.EmpleadoRepository;
import com.migestion.hr.dto.CreateEmpleadoRequest;
import com.migestion.hr.dto.EmpleadoResponse;
import com.migestion.hr.infrastructure.mapper.HrMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import java.time.LocalDate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateEmpleadoUseCase {

    private final EmpleadoRepository empleadoRepository;
    private final HrMapper hrMapper;
    private final PasswordEncoder passwordEncoder;

    public CreateEmpleadoUseCase(
            EmpleadoRepository empleadoRepository,
            HrMapper hrMapper,
            PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.hrMapper = hrMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmpleadoResponse execute(Long tenantId, CreateEmpleadoRequest request) {
        empleadoRepository.findByTenantIdAndEmailIgnoreCase(tenantId, request.email())
                .ifPresent(existing -> {
                    throw new BusinessRuleViolationException(
                            "EMPLEADO_EMAIL_DUPLICADO",
                            "An employee with email " + request.email() + " already exists"
                    );
                });

        Empleado empleado = Empleado.builder()
                .tenantId(tenantId)
                .nombre(request.nombre())
                .apellido(request.apellido())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .telefono(request.telefono())
                .especialidad(request.especialidad())
                .rol(request.rol() != null ? request.rol() : "staff")
                .tipoRemuneracion(request.tipoRemuneracion() != null ? request.tipoRemuneracion() : "fijo")
                .montoSueldoFijo(request.montoSueldoFijo())
                .frecuenciaPago(request.frecuenciaPago() != null ? request.frecuenciaPago() : "mensual")
                .costoHoraBase(request.costoHoraBase())
                .porcentajeComision(request.porcentajeComision())
                .fechaIngreso(request.fechaIngreso() != null ? request.fechaIngreso() : LocalDate.now())
                .build();

        Empleado saved = empleadoRepository.save(empleado);
        return hrMapper.toResponse(saved);
    }
}
