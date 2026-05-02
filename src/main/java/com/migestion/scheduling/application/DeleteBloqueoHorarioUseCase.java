package com.migestion.scheduling.application;

import com.migestion.scheduling.domain.BloqueoHorario;
import com.migestion.scheduling.domain.BloqueoHorarioRepository;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteBloqueoHorarioUseCase {

    private final BloqueoHorarioRepository bloqueoHorarioRepository;

    public DeleteBloqueoHorarioUseCase(BloqueoHorarioRepository bloqueoHorarioRepository) {
        this.bloqueoHorarioRepository = bloqueoHorarioRepository;
    }

    @Transactional
    public void execute(Long id, Long tenantId) {
        BloqueoHorario bloqueo = bloqueoHorarioRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("BloqueoHorario", id));
        bloqueo.setDeletedAt(Instant.now());
        bloqueoHorarioRepository.save(bloqueo);
    }
}
