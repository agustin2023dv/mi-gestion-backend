package com.migestion.marketing.infrastructure;

import com.migestion.marketing.application.DeleteReviewUseCase;
import com.migestion.marketing.application.GetMyReviewsUseCase;
import com.migestion.marketing.application.GetPendingReviewsUseCase;
import com.migestion.marketing.application.GetProductReviewsUseCase;
import com.migestion.marketing.application.GetTenantReviewsUseCase;
import com.migestion.marketing.application.ModerateReviewUseCase;
import com.migestion.marketing.application.RespondToReviewUseCase;
import com.migestion.marketing.application.SubmitProductReviewUseCase;
import com.migestion.marketing.application.SubmitTenantReviewUseCase;
import com.migestion.marketing.domain.ResenaProductoRepository;
import com.migestion.marketing.domain.ResenaTenantRepository;
import com.migestion.marketing.dto.CreateReviewRequest;
import com.migestion.marketing.dto.CreateTenantReviewRequest;
import com.migestion.marketing.dto.ModerateReviewRequest;
import com.migestion.marketing.dto.RespondToReviewRequest;
import com.migestion.marketing.dto.ReviewResponse;
import com.migestion.platform.dto.PageResponse;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resenas")
public class ReseñaController {

    private final SubmitProductReviewUseCase submitProductReviewUseCase;
    private final SubmitTenantReviewUseCase submitTenantReviewUseCase;
    private final GetProductReviewsUseCase getProductReviewsUseCase;
    private final GetTenantReviewsUseCase getTenantReviewsUseCase;
    private final GetMyReviewsUseCase getMyReviewsUseCase;
    private final GetPendingReviewsUseCase getPendingReviewsUseCase;
    private final ModerateReviewUseCase moderateReviewUseCase;
    private final RespondToReviewUseCase respondToReviewUseCase;
    private final DeleteReviewUseCase deleteReviewUseCase;
    private final ResenaProductoRepository resenaProductoRepository;
    private final ResenaTenantRepository resenaTenantRepository;

    public ReseñaController(
            SubmitProductReviewUseCase submitProductReviewUseCase,
            SubmitTenantReviewUseCase submitTenantReviewUseCase,
            GetProductReviewsUseCase getProductReviewsUseCase,
            GetTenantReviewsUseCase getTenantReviewsUseCase,
            GetMyReviewsUseCase getMyReviewsUseCase,
            GetPendingReviewsUseCase getPendingReviewsUseCase,
            ModerateReviewUseCase moderateReviewUseCase,
            RespondToReviewUseCase respondToReviewUseCase,
            DeleteReviewUseCase deleteReviewUseCase,
            ResenaProductoRepository resenaProductoRepository,
            ResenaTenantRepository resenaTenantRepository) {
        this.submitProductReviewUseCase = submitProductReviewUseCase;
        this.submitTenantReviewUseCase = submitTenantReviewUseCase;
        this.getProductReviewsUseCase = getProductReviewsUseCase;
        this.getTenantReviewsUseCase = getTenantReviewsUseCase;
        this.getMyReviewsUseCase = getMyReviewsUseCase;
        this.getPendingReviewsUseCase = getPendingReviewsUseCase;
        this.moderateReviewUseCase = moderateReviewUseCase;
        this.respondToReviewUseCase = respondToReviewUseCase;
        this.deleteReviewUseCase = deleteReviewUseCase;
        this.resenaProductoRepository = resenaProductoRepository;
        this.resenaTenantRepository = resenaTenantRepository;
    }

    /**
     * POST /api/v1/resenas/producto
     * Submits a review for a product.
     * Requires RESENA_CREAR permission (implicitly granted for authenticated clients).
     */
    @PostMapping("/producto")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitProductReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        if (user == null) {
            throw new BusinessRuleViolationException(
                    "AUTENTICACION_REQUERIDA",
                    "Se requiere autenticación para enviar una reseña");
        }

        ReviewResponse response = submitProductReviewUseCase.execute(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * POST /api/v1/resenas/tenant
     * Submits a review for the tenant (store).
     * Requires RESENA_CREAR permission (implicitly granted for authenticated clients).
     */
    @PostMapping("/tenant")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitTenantReview(
            @Valid @RequestBody CreateTenantReviewRequest request,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        if (user == null) {
            throw new BusinessRuleViolationException(
                    "AUTENTICACION_REQUERIDA",
                    "Se requiere autenticación para enviar una reseña");
        }

        ReviewResponse response = submitTenantReviewUseCase.execute(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/resenas/producto/{productoId}
     * Retrieves published reviews for a specific product.
     * This endpoint is public.
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable Long productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = buildPageable(page, size, sort, 50);
        PageResponse<ReviewResponse> response = getProductReviewsUseCase.execute(productoId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/resenas/tenant/{tenantId}
     * Retrieves published reviews for a specific tenant.
     * This endpoint is public.
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getTenantReviews(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = buildPageable(page, size, sort, 50);
        PageResponse<ReviewResponse> response = getTenantReviewsUseCase.execute(tenantId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/resenas/me
     * Retrieves all reviews submitted by the authenticated client.
     * Requires authentication.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        if (user == null) {
            throw new BusinessRuleViolationException(
                    "AUTENTICACION_REQUERIDA",
                    "Se requiere autenticación para ver tus reseñas");
        }

        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        PageResponse<ReviewResponse> response = getMyReviewsUseCase.execute(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/resenas/pendientes
     * Retrieves reviews pending moderation for the current tenant.
     * Requires RESENA_MODERAR permission (TenantAdmin, TenantManager).
     */
    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        if (user == null) {
            throw new BusinessRuleViolationException(
                    "AUTENTICACION_REQUERIDA",
                    "Se requiere autenticación");
        }

        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        PageResponse<ReviewResponse> response = getPendingReviewsUseCase.execute(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * PATCH /api/v1/resenas/{id}/moderar
     * Moderates a review (approve or reject).
     * Requires RESENA_MODERAR permission (TenantAdmin, TenantManager).
     */
    @PatchMapping("/{id}/moderar")
    public ResponseEntity<ApiResponse<ReviewResponse>> moderateReview(
            @PathVariable Long id,
            @Valid @RequestBody ModerateReviewRequest request,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        if (user == null) {
            throw new BusinessRuleViolationException(
                    "AUTENTICACION_REQUERIDA",
                    "Se requiere autenticación");
        }

        // Try to find and moderate as product review first
        var productReview = resenaProductoRepository.findByIdAndTenantId(id, user.getTenantId());
        if (productReview.isPresent()) {
            ReviewResponse response = moderateReviewUseCase.executeProductReview(id, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        // Try to find and moderate as tenant review
        var tenantReview = resenaTenantRepository.findByIdAndTenantId(id, user.getTenantId());
        if (tenantReview.isPresent()) {
            ReviewResponse response = moderateReviewUseCase.executeTenantReview(id, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        throw new BusinessRuleViolationException(
                "RESENA_NO_ENCONTRADA",
                "La reseña no existe o no pertenece a este tenant");
    }

    /**
     * POST /api/v1/resenas/{id}/responder
     * Adds a public response to a published review.
     * Requires RESENA_RESPONDER permission (TenantAdmin, TenantManager).
     */
    @PostMapping("/{id}/responder")
    public ResponseEntity<ApiResponse<ReviewResponse>> respondToReview(
            @PathVariable Long id,
            @Valid @RequestBody RespondToReviewRequest request,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        if (user == null) {
            throw new BusinessRuleViolationException(
                    "AUTENTICACION_REQUERIDA",
                    "Se requiere autenticación");
        }

        // Try to find and respond to product review first
        var productReview = resenaProductoRepository.findByIdAndTenantId(id, user.getTenantId());
        if (productReview.isPresent()) {
            ReviewResponse response = respondToReviewUseCase.respondToProductReview(id, request.respuesta());
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        // Try to find and respond to tenant review
        var tenantReview = resenaTenantRepository.findByIdAndTenantId(id, user.getTenantId());
        if (tenantReview.isPresent()) {
            ReviewResponse response = respondToReviewUseCase.respondToTenantReview(id, request.respuesta());
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        throw new BusinessRuleViolationException(
                "RESENA_NO_ENCONTRADA",
                "La reseña no existe o no pertenece a este tenant");
    }

    /**
     * DELETE /api/v1/resenas/{id}
     * Deletes a review (soft delete).
     * Requires RESENA_MODERAR permission (TenantAdmin only).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        if (user == null) {
            throw new BusinessRuleViolationException(
                    "AUTENTICACION_REQUERIDA",
                    "Se requiere autenticación");
        }

        // Try to find and delete as product review first
        var productReview = resenaProductoRepository.findByIdAndTenantId(id, user.getTenantId());
        if (productReview.isPresent()) {
            deleteReviewUseCase.deleteProductReview(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Review deleted successfully.");
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        // Try to find and delete as tenant review
        var tenantReview = resenaTenantRepository.findByIdAndTenantId(id, user.getTenantId());
        if (tenantReview.isPresent()) {
            deleteReviewUseCase.deleteTenantReview(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Review deleted successfully.");
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        throw new BusinessRuleViolationException(
                "RESENA_NO_ENCONTRADA",
                "La reseña no existe o no pertenece a este tenant");
    }

    private Pageable buildPageable(int page, int size, String sort, int maxSize) {
        if (page < 0) {
            page = 0;
        }

        if (size < 1 || size > maxSize) {
            size = Math.min(10, maxSize);
        }

        try {
            String[] sortParts = sort.split(",");
            if (sortParts.length == 2) {
                String property = sortParts[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParts[1].toUpperCase());
                return PageRequest.of(page, size, Sort.by(direction, property));
            }
        } catch (Exception ignored) {
            // Fall back to default
        }

        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
