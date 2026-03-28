package org.example.mappro.config;

import org.springframework.stereotype.Component;

@Component
public class RoleEndpointsConfig {

    // 🔒 Только ADMIN
    public static final String[] ADMIN_ENDPOINTS = {
            "/api/v1/bug-reports",           // просмотр багов
            "/api/v1/bug-reports/*/verification",
            "/api/v3/auth/**",
    };

    // 🧑‍💻 ADMIN и DEVELOPER
    public static final String[] DEVELOPER_ENDPOINTS = {
            "/api/v1/users",
            "/api/v1/notifications/**"
    };

    // 👤 USER, ADMIN, DEVELOPER — базовые права
    public static final String[] USER_ENDPOINTS = {
    };
}
