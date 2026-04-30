package com.migestion.hr.application;

import com.migestion.hr.domain.EmpleadoRepository;
import com.migestion.hr.dto.EmpleadoResponse;
import com.migestion.hr.infrastructure.mapper.HrMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllEmpleadosUseCase {

    private final EmpleadoRepository empleadoRepository;
    private final HrMapper hrMapper;

    public GetAllEmpleadosUseCase(EmpleadoRepository empleadoRepository, HrMapper hrMapper) {
        this.empleadoRepository = empleadoRepository;
        this.hrMapper = hrMapper;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> execute(Long tenantId, Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return empleadoRepository.findAllByTenantIdAndIsActiveTrue(tenantId).stream()
                    .map(hrMapper::toResponse)
                    .toList();
        }
        return empleadoRepository.findAllByTenantId(tenantId).stream()
                .map(hrMapper::toResponse)
                .toList();
    }
}
