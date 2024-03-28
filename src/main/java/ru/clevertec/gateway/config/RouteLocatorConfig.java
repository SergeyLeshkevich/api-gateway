package ru.clevertec.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.gateway.filter.AuthorizationFilter;
import ru.clevertec.gateway.util.RouteValidator;

/**
 * Configuration class for setting up route locators for different services.
 * This class defines the routes for news, comments, API, and authentication services.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Configuration
public class RouteLocatorConfig {

    /**
     * Defines the route locator for the news service.
     *
     * @param builder the RouteLocatorBuilder to create the route locator.
     * @return a RouteLocator for the news service.
     */
    @Bean
    public RouteLocator customRouteLocatorNewsService(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("news-service",
                        r -> r.path("/news/**")
                                .uri("lb://news-service"))
                .build();
    }

    /**
     * Defines the route locator for the comment service.
     *
     * @param builder the RouteLocatorBuilder to create the route locator.
     * @return a RouteLocator for the comment service.
     */
    @Bean
    public RouteLocator customRouteLocatorCommentService(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("comment-service",
                        r -> r.path("/comments/**")
                                .uri("lb://comment-service"))
                .build();
    }

    /**
     * Defines the route locator for the API news service with authorization filters.
     *
     * @param builder         the RouteLocatorBuilder to create the route locator.
     * @param routeValidator  the RouteValidator to validate the routes.
     * @param template        the RestTemplate for making HTTP requests.
     * @return a RouteLocator for the API news service.
     */

    @Bean
    public RouteLocator customRouteLocatorNewsApi(RouteLocatorBuilder builder,
                                                  RouteValidator routeValidator,
                                                  RestTemplate template) {
        return builder.routes()
                .route("api-news",
                        r -> r.path("/api/news/**", "/api/comments/**")
                                .filters(f -> f.filter(new AuthorizationFilter(template, routeValidator)))
                                .uri("lb://api-news"))
                .build();
    }

    /**
     * Defines the route locator for the authentication service.
     *
     * @param builder the RouteLocatorBuilder to create the route locator.
     * @return a RouteLocator for the authentication service.
     */
    @Bean
    public RouteLocator authFilterBean(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service",
                        r -> r.path("/auth/**","/users/**")
                        .uri("lb://auth-service"))
                .build();
    }
}
