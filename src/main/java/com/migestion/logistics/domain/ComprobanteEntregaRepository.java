package com.migestion.logistics.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComprobanteEntregaRepository extends JpaRepository<ComprobanteEntrega, Long> {

    Optional<ComprobanteEntrega> findByEntregaId(Long entregaId);

    Optional<ComprobanteEntrega> findByPedidoId(Long pedidoId);
}
