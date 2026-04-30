package com.migestion.scheduling.application;

import com.migestion.scheduling.domain.BloqueoHorario;
import com.migestion.scheduling.domain.BloqueoHorarioRepository;
import com.migestion.scheduling.dto.BloqueoHorarioRequest;
import com.migestion.scheduling.dto.BloqueoHorarioResponse;
import com.migestion.scheduling.infrastructure.mapper.SchedulingMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBloqueoHorarioUseCase {

    private final BloqueoHorarioRepository bloqueoHorarioRepository;
    private final SchedulingMapper schedulingMapper;

    public CreateBloqueoHorarioUseCase(
            BloqueoHorarioRepository bloqueoHorarioRepository,
            SchedulingMapper schedulingMapper) {
        this.bloqueoHorarioRepository = bloqueoHorarioRepository;
        this.schedulingMapper = schedulingMapper;
    }

    @Transactional
    public BloqueoHorarioResponse execute(Long tenantId, BloqueoHorarioRequest request) {
        if (request.horaInicio().isAfter(request.horaFin()) || request.horaInicio().equals(request.horaFin())) {
            throw new BusinessRuleViolationException(
                    "BLOQUEO_RANGO_INVALIDO",
                    "horaInicio must be before horaFin"
            );
        }

        BloqueoHorario bloqueo = BloqueoHorario.builder()
                .tenantId(tenantId)
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .motivo(request.motivo())
                .tipoBloqueo(request.tipoBloqueo() != null ? request.tipoBloqueo() : "manual")
                .isRecurrente(request.isRecurrente() != null && request.isRecurrente())
                .reglaRecurrencia(request.reglaRecurrencia())
                .build();

        BloqueoHorario saved = bloqueoHorarioRepository.save(bloqueo);
        return schedulingMapper.toResponse(saved);
    }
}
