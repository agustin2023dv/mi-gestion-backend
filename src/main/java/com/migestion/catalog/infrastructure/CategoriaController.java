package com.migestion.catalog.infrastructure;

import com.migestion.catalog.application.GetAllCategoriasUseCase;
import com.migestion.catalog.application.GetCategoriaByIdUseCase;
import com.migestion.catalog.application.GetSubcategoriasByCategoriaUseCase;
import com.migestion.catalog.dto.CategoriaDetailResponse;
import com.migestion.catalog.dto.CategoriaListItemResponse;
import com.migestion.catalog.dto.PageResponse;
import com.migestion.catalog.dto.SubcategoriaResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoriaController {

    private final GetAllCategoriasUseCase getAllCategoriasUseCase;
    private final GetCategoriaByIdUseCase getCategoriaByIdUseCase;
    private final GetSubcategoriasByCategoriaUseCase getSubcategoriasByCategoriaUseCase;

    public CategoriaController(
            GetAllCategoriasUseCase getAllCategoriasUseCase,
            GetCategoriaByIdUseCase getCategoriaByIdUseCase,
            GetSubcategoriasByCategoriaUseCase getSubcategoriasByCategoriaUseCase
    ) {
        this.getAllCategoriasUseCase = getAllCategoriasUseCase;
        this.getCategoriaByIdUseCase = getCategoriaByIdUseCase;
        this.getSubcategoriasByCategoriaUseCase = getSubcategoriasByCategoriaUseCase;
    }

    @GetMapping("/api/v1/categorias")
    public ResponseEntity<Map<String, Object>> getAllCategorias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "nombre,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<CategoriaListItemResponse> response = getAllCategoriasUseCase.execute(pageable);
        return ResponseEntity.ok(successBody(response));
    }

    @GetMapping("/api/v1/categorias/{id}")
    public ResponseEntity<Map<String, Object>> getCategoriaById(@PathVariable Long id) {
        CategoriaDetailResponse response = getCategoriaByIdUseCase.execute(id);
        return ResponseEntity.ok(successBody(response));
    }

    @GetMapping("/api/v1/subcategorias")
    public ResponseEntity<Map<String, Object>> getSubcategoriasByCategoria(
            @RequestParam Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "nombre,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<SubcategoriaResponse> response = getSubcategoriasByCategoriaUseCase.execute(categoriaId, pageable);
        return ResponseEntity.ok(successBody(response));
    }

    private Pageable buildPageable(int page, int size, String sort) {
        if (page < 0) {
            throw new BusinessRuleViolationException("INVALID_PAGE_NUMBER", "page must be greater than or equal to 0");
        }

        if (size < 1 || size > 100) {
            throw new BusinessRuleViolationException("INVALID_PAGE_SIZE", "size must be between 1 and 100");
        }

        String[] sortParts = sort.split(",", 2);
        String property = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(direction, property));
    }

    private Map<String, Object> successBody(Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("error", null);
        response.put("timestamp", Instant.now());
        return response;
    }
}