package com.migestion.catalog.infrastructure;

import com.migestion.catalog.application.CreateProductoUseCase;
import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.domain.ProductoRepository;
import com.migestion.catalog.dto.CreateProductoRequest;
import com.migestion.catalog.dto.ProductoResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final CreateProductoUseCase createProductoUseCase;
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public ProductoController(
            CreateProductoUseCase createProductoUseCase,
            ProductoRepository productoRepository,
            ProductoMapper productoMapper
    ) {
        this.createProductoUseCase = createProductoUseCase;
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateProductoRequest request) {
        ProductoResponse response = createProductoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(successBody(response));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        requireTenantContext();
        Pageable pageable = buildPageable(page, size, sort);
        Page<ProductoResponse> productoPage = productoRepository.findAll(pageable).map(productoMapper::toResponse);

        Map<String, Object> pageData = new LinkedHashMap<>();
        pageData.put("content", productoPage.getContent());
        pageData.put("pageNumber", productoPage.getNumber());
        pageData.put("pageSize", productoPage.getSize());
        pageData.put("totalElements", productoPage.getTotalElements());
        pageData.put("totalPages", productoPage.getTotalPages());
        pageData.put("hasNext", productoPage.hasNext());
        pageData.put("hasPrevious", productoPage.hasPrevious());

        return ResponseEntity.ok(successBody(pageData));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Long tenantId = requireTenantContext();
        Producto producto = productoRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return ResponseEntity.ok(successBody(productoMapper.toResponse(producto)));
    }

    private Long requireTenantContext() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required for catalog operations"
            );
        }
        return tenantId;
    }

    private Pageable buildPageable(int page, int size, String sort) {
        if (size < 1 || size > 100) {
            throw new BusinessRuleViolationException(
                    "INVALID_PAGE_SIZE",
                    "size must be between 1 and 100"
            );
        }

        String[] sortParts = sort.split(",", 2);
        String property = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

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