package ru.clevertec.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.clevertec.exceptionhandlerstarter.exception.NoAuthorizationException;
import ru.clevertec.gateway.entity.JwtResponse;
import ru.clevertec.gateway.util.RouteValidator;


import java.util.List;

/**
 * Filter class for handling authorization in a gateway.
 * This filter checks for JWT tokens in the request headers and validates them to control access to routes.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@RequiredArgsConstructor
public class AuthorizationFilter implements GatewayFilter {

    private final RestTemplate template;
    private final RouteValidator validator;

    /**
     * Filters incoming HTTP requests for authorization.
     *
     * @param exchange the current server web exchange context.
     * @param chain    the gateway filter chain.
     * @return a Mono<Void> indicating when request handling is complete.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (validator.isOpenEndpoint(request)) {
            return chain.filter(exchange);
        }
        String token = getToken(exchange);
        JwtResponse jwtResponse = getJwtResponse(token);
        if (validator.checkAccess(request, jwtResponse.getRoles())) {
            exchange.getRequest().mutate()
                    .header("X-User-UUID", String.valueOf(jwtResponse.getUuid()))
                    .header("X-User-Name", jwtResponse.getUsername())
                    .build();
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    /**
     * Extracts the JWT token from the request headers.
     *
     * @param exchange the current server web exchange context.
     * @return the JWT token as a String.
     * @throws NoAuthorizationException if the authorization header is missing or invalid.
     */
    private String getToken(ServerWebExchange exchange) {
        List<String> headers = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (headers == null || headers.isEmpty()) {
            throw new NoAuthorizationException("Missing authorization header");
        }

        String token = headers.get(0);
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        throw new NoAuthorizationException("Invalid authorization header format");
    }

    /**
     * Retrieves the JwtResponse from the authentication service using the provided JWT token.
     *
     * @param token the JWT token to validate.
     * @return a JwtResponse object containing the user's details and roles.
     * @throws NoAuthorizationException if the token is invalid or the response is null.
     */
    private JwtResponse getJwtResponse(String token) {
        JwtResponse jwtResponse = template.postForEntity("http://localhost:8082/auth/validate",
                token, JwtResponse.class).getBody();
        if (jwtResponse == null) {
            throw new NoAuthorizationException("Token is invalid");
        }
        return jwtResponse;
    }
}