package com.migestion.payments.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByIdAndTenantId(Long id, Long tenantId);

    List<Pago> findAllByPedidoIdAndTenantId(Long pedidoId, Long tenantId);

    Optional<Pago> findByTransactionIdAndTenantId(String transactionId, Long tenantId);
}
