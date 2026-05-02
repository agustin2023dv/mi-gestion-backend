package com.migestion.hr.application;

import com.migestion.hr.domain.EmpleadoRepository;
import com.migestion.hr.dto.EmpleadoResponse;
import com.migestion.hr.infrastructure.mapper.HrMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetEmpleadoByIdUseCase {

    private final EmpleadoRepository empleadoRepository;
    private final HrMapper hrMapper;

    public GetEmpleadoByIdUseCase(EmpleadoRepository empleadoRepository, HrMapper hrMapper) {
        this.empleadoRepository = empleadoRepository;
        this.hrMapper = hrMapper;
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse execute(Long id, Long tenantId) {
        return empleadoRepository.findByIdAndTenantId(id, tenantId)
                .map(hrMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", id));
    }
}
