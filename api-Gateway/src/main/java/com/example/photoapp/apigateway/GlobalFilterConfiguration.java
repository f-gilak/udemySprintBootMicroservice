package com.example.photoapp.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class GlobalFilterConfiguration {

    @Order(2)
    @Bean
    public GlobalFilter secondPreFilter() {
        return ((exchange, chain) -> {
            log.info("Second Pre Filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Second Post Filter");
            }));
        });
    }

    @Order(3)
    @Bean
    public GlobalFilter thirdPreFilter() {
        return ((exchange, chain) -> {
            log.info("Third Pre Filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("First Post Filter");
            }));
        });
    }
}
