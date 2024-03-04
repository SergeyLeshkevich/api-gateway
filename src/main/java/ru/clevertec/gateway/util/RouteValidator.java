package ru.clevertec.gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * Component responsible for validating access to different API routes based on user roles.
 * It maps specific URL patterns to roles and determines if a request should be allowed based on the user's roles.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Component
public class RouteValidator {
    private static String ROLE_ADMIN = "ROLE_ADMIN";
    private static String ROLE_SUBSCRIBER = "ROLE_SUBSCRIBER";
    private static String ROLE_JOURNALIST = "ROLE_JOURNALIST";
    private final Map<String, Set<String>> roleUrlAccessMap;
    public static final List<String> openApiEndpoints = List.of(
            "/api/comments/\\d+",
            "/api/comments/\\d+/news/\\d+",
            "/api/comments",
            "/api/comments/news/\\d+",
            "/api/comments/search",
            "/api/news/\\d+",
            "/api/news",
            "/api/news/search"
    );

    /**
     * Initializes the route validator with specific URL patterns and their corresponding allowed roles.
     */
    public RouteValidator() {
        roleUrlAccessMap = new HashMap<>();
        roleUrlAccessMap.put("/api/comments/archive/\\d+", Set.of(ROLE_ADMIN,ROLE_SUBSCRIBER));
        roleUrlAccessMap.put("/api/comments/archive", Set.of(ROLE_ADMIN));
        roleUrlAccessMap.put("/api/comments", Set.of(ROLE_ADMIN, ROLE_SUBSCRIBER));
        roleUrlAccessMap.put("/api/comments/\\d+", Set.of(ROLE_ADMIN,ROLE_SUBSCRIBER));
        roleUrlAccessMap.put("/api/comments/news/\\d+", Set.of(ROLE_ADMIN,ROLE_SUBSCRIBER,ROLE_JOURNALIST));
        roleUrlAccessMap.put("/api/news/archive/\\d+",Set.of(ROLE_ADMIN,ROLE_SUBSCRIBER));
        roleUrlAccessMap.put("/api/news/archive",Set.of(ROLE_ADMIN));
        roleUrlAccessMap.put("/api/news",Set.of(ROLE_ADMIN,ROLE_JOURNALIST));
        roleUrlAccessMap.put("/api/news/\\d+",Set.of(ROLE_ADMIN,ROLE_JOURNALIST));
    }

    /**
     * Checks if the user has access to the requested URL based on their roles.
     *
     * @param request    the incoming server HTTP request.
     * @param userRoles  the set of roles associated with the user.
     * @return true if the user has access, false otherwise.
     */
    public boolean checkAccess(ServerHttpRequest request, Set<String> userRoles) {
        String requestUrl = request.getURI().getPath();

        String keyByUrl = roleUrlAccessMap.keySet().stream()
                .filter(requestUrl::matches)
                .findAny()
                .orElseThrow(NoSuchElementException::new);

        Set<String> allowedRoles = roleUrlAccessMap.get(keyByUrl);
        if (allowedRoles == null) {
            return false;
        }

        for (String role : userRoles) {
            if (allowedRoles.contains(role)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the requested endpoint is open for public access without authentication.
     *
     * @param request the incoming server HTTP request.
     * @return true if the endpoint is open, false otherwise.
     */
    public boolean isOpenEndpoint(ServerHttpRequest request) {
        boolean checkHasUrl = openApiEndpoints.stream()
                .anyMatch(url -> request.getURI().getPath().matches(url));
        String method = request.getMethod().name();
        return checkHasUrl && method.equals("GET");
    }

}