package com.migestion.catalog.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Page<Categoria> findByIsActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = "subcategorias")
    Optional<Categoria> findByIdAndIsActiveTrue(Long id);

    boolean existsByIdAndIsActiveTrue(Long id);
}