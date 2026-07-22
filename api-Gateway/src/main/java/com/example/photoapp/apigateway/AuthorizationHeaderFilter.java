package com.example.photoapp.apigateway;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${token.secret_key}")
    private String tokenSecret;

    AuthorizationHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No AUthorization Header", HttpStatus.UNAUTHORIZED);
            }
            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String jwt = token.replace("Bearer ", "");
            if (!isJwtValid((jwt))) {
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }

    private boolean isJwtValid(String jwt) {

        SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
//        byte[] secretKeyByte = Base64.getEncoder().encode(tokenSecret.getBytes());
//        SecretKey key = new SecretKeySpec(secretKeyByte, SignatureAlgorithm.HS512.getJcaName());
        JwtParser jwtParser = Jwts.parser().setSigningKey(key).build();
        try {
            Jwt<Header, Claims> claimsJwt = (Jwt<Header, Claims>) jwtParser.parse(jwt);
            String subject = claimsJwt.getBody().getSubject();
            if (StringUtils.hasText(subject)) return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String noAUthorizationHeader, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        //put configuraion properties here
    }
}
