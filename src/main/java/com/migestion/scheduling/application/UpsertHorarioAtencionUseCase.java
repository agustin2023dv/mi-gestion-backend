package com.migestion.scheduling.application;

import com.migestion.scheduling.domain.HorarioAtencion;
import com.migestion.scheduling.domain.HorarioAtencionRepository;
import com.migestion.scheduling.dto.HorarioAtencionRequest;
import com.migestion.scheduling.dto.HorarioAtencionResponse;
import com.migestion.scheduling.infrastructure.mapper.SchedulingMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpsertHorarioAtencionUseCase {

    private final HorarioAtencionRepository horarioAtencionRepository;
    private final SchedulingMapper schedulingMapper;

    public UpsertHorarioAtencionUseCase(
            HorarioAtencionRepository horarioAtencionRepository,
            SchedulingMapper schedulingMapper) {
        this.horarioAtencionRepository = horarioAtencionRepository;
        this.schedulingMapper = schedulingMapper;
    }

    @Transactional
    public HorarioAtencionResponse execute(Long tenantId, HorarioAtencionRequest request) {
        if (request.horaApertura().isAfter(request.horaCierre()) || request.horaApertura().equals(request.horaCierre())) {
            throw new BusinessRuleViolationException(
                    "HORARIO_RANGO_INVALIDO",
                    "horaApertura must be before horaCierre"
            );
        }

        HorarioAtencion horario = horarioAtencionRepository
                .findByTenantIdAndDiaSemana(tenantId, request.diaSemana())
                .orElseGet(() -> HorarioAtencion.builder()
                        .tenantId(tenantId)
                        .diaSemana(request.diaSemana())
                        .build());

        horario.setHoraApertura(request.horaApertura());
        horario.setHoraCierre(request.horaCierre());
        horario.setActivo(request.isActivo() == null || request.isActivo());

        HorarioAtencion saved = horarioAtencionRepository.save(horario);
        return schedulingMapper.toResponse(saved);
    }
}
