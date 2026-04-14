package com.migestion.shared.config;

import com.migestion.shared.security.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hibernate.Session;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@FilterDef(name = HibernateFilterConfig.TENANT_FILTER_NAME, parameters = @ParamDef(name = HibernateFilterConfig.TENANT_PARAMETER_NAME, type = Long.class))
public class HibernateFilterConfig {

    public static final String TENANT_FILTER_NAME = "tenantFilter";
    public static final String TENANT_PARAMETER_NAME = "tenantId";

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> tenantHibernateSessionFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantHibernateSessionFilter(entityManager));
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        return registrationBean;
    }

    private static final class TenantHibernateSessionFilter extends OncePerRequestFilter {

        private final EntityManager entityManager;

        private TenantHibernateSessionFilter(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {

            Long tenantId = TenantContext.getTenantId();
            if (tenantId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Session session = entityManager.unwrap(Session.class);
            boolean filterEnabled = false;
            try {
                session.enableFilter(TENANT_FILTER_NAME).setParameter(TENANT_PARAMETER_NAME, tenantId);
                filterEnabled = true;
            } catch (RuntimeException ignored) {
                // Ignore when no tenant-filtered entities are involved in the current unit of work.
            }

            try {
                filterChain.doFilter(request, response);
            } finally {
                if (filterEnabled) {
                    session.disableFilter(TENANT_FILTER_NAME);
                }
            }
        }
    }
}
