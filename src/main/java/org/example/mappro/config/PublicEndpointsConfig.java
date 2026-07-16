package org.example.mappro.config;

import org.springframework.stereotype.Component;

@Component
public class PublicEndpointsConfig {

    // ✅ только GET-запросы без авторизации
    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v3/roles",         // просмотр ролей
            "/api/v1/bug-reports/**",           // просмотр багов
            "/api/v1/notifications/bug-report/*/status-changes",           // просмотр багов
            "/images/**",            // загрузка изображений
            "/error",
            "/api/v3/roles",
            "/api/v3/auth/authenticate",
            "/api/tiles/**",
            "/api/admin/import/**",
            "/api/**",
            "/api/tiles/**"
    };

    // ✅ только POST-запросы без авторизации
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
