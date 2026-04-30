package com.migestion.scheduling.application;

import com.migestion.scheduling.domain.ExcepcionHorario;
import com.migestion.scheduling.domain.ExcepcionHorarioRepository;
import com.migestion.scheduling.dto.ExcepcionHorarioRequest;
import com.migestion.scheduling.dto.ExcepcionHorarioResponse;
import com.migestion.scheduling.infrastructure.mapper.SchedulingMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateExcepcionHorarioUseCase {

    private final ExcepcionHorarioRepository excepcionHorarioRepository;
    private final SchedulingMapper schedulingMapper;

    public CreateExcepcionHorarioUseCase(
            ExcepcionHorarioRepository excepcionHorarioRepository,
            SchedulingMapper schedulingMapper) {
        this.excepcionHorarioRepository = excepcionHorarioRepository;
        this.schedulingMapper = schedulingMapper;
    }

    @Transactional
    public ExcepcionHorarioResponse execute(Long tenantId, ExcepcionHorarioRequest request) {
        excepcionHorarioRepository.findByTenantIdAndFecha(tenantId, request.fecha())
                .ifPresent(existing -> {
                    throw new BusinessRuleViolationException(
                            "EXCEPCION_DUPLICADA",
                            "An exception already exists for date " + request.fecha()
                    );
                });

        boolean cerrado = request.isCerradoCompleto() != null && request.isCerradoCompleto();
        if (!cerrado && (request.horaApertura() == null || request.horaCierre() == null)) {
            throw new BusinessRuleViolationException(
                    "HORARIO_REQUERIDO",
                    "horaApertura and horaCierre are required when isCerradoCompleto is false"
            );
        }

        ExcepcionHorario excepcion = ExcepcionHorario.builder()
                .tenantId(tenantId)
                .fecha(request.fecha())
                .horaApertura(request.horaApertura())
                .horaCierre(request.horaCierre())
                .isCerradoCompleto(cerrado)
                .motivo(request.motivo())
                .afectaBooking(request.afectaBooking() == null || request.afectaBooking())
                .afectaPedidos(request.afectaPedidos() == null || request.afectaPedidos())
                .build();

        ExcepcionHorario saved = excepcionHorarioRepository.save(excepcion);
        return schedulingMapper.toResponse(saved);
    }
}
