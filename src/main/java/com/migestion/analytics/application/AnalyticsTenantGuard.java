package com.migestion.analytics.application;

import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;

final class AnalyticsTenantGuard {

    private AnalyticsTenantGuard() {
        // Utility class
    }

    static void requireTenantAccess(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessRuleViolationException("TENANT_ID_REQUIRED", "tenantId must be greater than 0");
        }

        Long tenantFromContext = TenantContext.getTenantId();
        if (tenantFromContext != null && !tenantFromContext.equals(tenantId)) {
            throw new BusinessRuleViolationException("TENANT_ACCESS_DENIED", "tenantId does not match authenticated tenant context");
        }
    }
}