package com.migestion.platform.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {

    Optional<SuperAdmin> findByEmailIgnoreCase(String email);
}
