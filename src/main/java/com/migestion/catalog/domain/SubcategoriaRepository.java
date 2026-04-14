package com.migestion.catalog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {

    Page<Subcategoria> findByIsActiveTrue(Pageable pageable);

    Page<Subcategoria> findByCategoriaIdAndIsActiveTrue(Long categoriaId, Pageable pageable);
}