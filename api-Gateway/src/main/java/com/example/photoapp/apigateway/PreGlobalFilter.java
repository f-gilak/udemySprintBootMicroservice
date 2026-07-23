package com.example.photoapp.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
public class PreGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("First pre Global Filter");
        String path = exchange.getRequest().getPath().toString();
        log.info("First Path: {}", path);
        logHeader(exchange);
        return chain.filter(exchange);
    }

    private void logHeader(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Set<String> headerNames = headers.keySet();
        headerNames.forEach(headerName -> {
            String headerValue = headers.getFirst(headerName);
            log.info("Header {}: {}", headerName, headerValue);
        });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
