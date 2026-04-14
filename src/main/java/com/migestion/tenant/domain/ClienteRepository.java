package com.migestion.tenant.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findFirstByEmailIgnoreCase(String email);
}