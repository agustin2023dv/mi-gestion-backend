package com.migestion.logistics.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmaDigitalRepository extends JpaRepository<FirmaDigital, Long> {

    Optional<FirmaDigital> findByTokenUnico(UUID tokenUnico);

    Optional<FirmaDigital> findByEntregaId(Long entregaId);
}
