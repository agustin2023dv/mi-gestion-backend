package com.migestion.shared.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to log every incoming HTTP request and outgoing response with timing.
 * Useful for debugging and tracking endpoint usage.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2) // Runs after CorrelationIdFilter but before TenantFilter and Security
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        log.info(">> Request: {} {}{}", method, requestURI, queryString);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            if (status >= 400 && status < 500) {
                log.warn("<< Response: {} {}{} - Status: {} ({}ms)", method, requestURI, queryString, status, duration);
            } else if (status >= 500) {
                log.error("<< Response: {} {}{} - Status: {} ({}ms)", method, requestURI, queryString, status, duration);
            } else {
                log.info("<< Response: {} {}{} - Status: {} ({}ms)", method, requestURI, queryString, status, duration);
            }
        }
    }
}
