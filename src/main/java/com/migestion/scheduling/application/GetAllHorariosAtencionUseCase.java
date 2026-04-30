package com.migestion.scheduling.application;

import com.migestion.scheduling.domain.HorarioAtencionRepository;
import com.migestion.scheduling.dto.HorarioAtencionResponse;
import com.migestion.scheduling.infrastructure.mapper.SchedulingMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllHorariosAtencionUseCase {

    private final HorarioAtencionRepository horarioAtencionRepository;
    private final SchedulingMapper schedulingMapper;

    public GetAllHorariosAtencionUseCase(
            HorarioAtencionRepository horarioAtencionRepository,
            SchedulingMapper schedulingMapper) {
        this.horarioAtencionRepository = horarioAtencionRepository;
        this.schedulingMapper = schedulingMapper;
    }

    @Transactional(readOnly = true)
    public List<HorarioAtencionResponse> execute(Long tenantId) {
        return horarioAtencionRepository.findAllByTenantId(tenantId).stream()
                .map(schedulingMapper::toResponse)
                .toList();
    }
}
