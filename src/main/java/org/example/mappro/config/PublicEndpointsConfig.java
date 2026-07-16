package org.example.mappro.config;

import org.springframework.stereotype.Component;

@Component
public class PublicEndpointsConfig {

    public static final String[] PUBLIC_GET_ENDPOINTS = {
        "/api/v3/roles",
        "/api/v1/bug-reports/**",
        "/api/v1/notifications/bug-report/*/status-changes",
        "/images/**",
        "/error",
        "/api/v3/roles",
        "/api/v3/auth/authenticate",
        "/api/tiles/**",
        "/api/admin/import/**",
        "/api/**",
        "/api/tiles/**"
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
        "/api/v3/auth/authenticate",
        "/api/v3/roles",
        "/api/admin/*",
        "/api/tiles/**",
        "/api/admin/import/**",
        "/api/**",
        "/api/tiles/**"
    };
}
