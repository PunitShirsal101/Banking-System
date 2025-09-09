package com.spunit.common.web;

import com.spunit.common.CommonConstants;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Simple correlation-id propagation filter for WebFlux services.
 * - Reads X-Correlation-Id or generates a new UUID if absent.
 * - Adds header to the response.
 * - Puts value into MDC under key "correlationId" for JSON logging linkage.
 */
@Component
public class CorrelationIdFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String headerName = CommonConstants.CORRELATION_ID_HEADER;
        String correlationId = request.getHeaders().getFirst(headerName);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set(headerName, correlationId);

        MDC.put("correlationId", correlationId);
        String finalCorrelationId = correlationId;
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("correlationId", finalCorrelationId))
                .doFinally(sig -> MDC.remove("correlationId"));
    }
}
