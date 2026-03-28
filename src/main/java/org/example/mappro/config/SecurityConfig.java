package org.example.mappro.config;

import lombok.extern.slf4j.Slf4j;
import org.example.mappro.auth.service.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Укажите точный origin вашего фронтенда (не "*")
        config.setAllowedOrigins(List.of("http://localhost:8081", "https://bug-tracker-gtt.ru/"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        // Разрешаем отправку кук (credentials)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 👇 Публичные GET и POST из твоей конфигурации
                        .requestMatchers(HttpMethod.GET, PublicEndpointsConfig.PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, PublicEndpointsConfig.PUBLIC_POST_ENDPOINTS).permitAll()

                        .requestMatchers(RoleEndpointsConfig.ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(RoleEndpointsConfig.DEVELOPER_ENDPOINTS).hasAnyRole("ADMIN", "DEVELOPER")
                        .requestMatchers(RoleEndpointsConfig.USER_ENDPOINTS).hasAnyRole("USER", "ADMIN", "DEVELOPER")

                        // 👇 Остальные GET, POST, PUT, DELETE — только авторизованные
                        .requestMatchers("/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((request, response, ex) -> {
                            log.error("Access denied for path: {}", request.getRequestURI());
                            response.sendError(403, "Access Denied");
                        })
                        .authenticationEntryPoint((request, response, ex) -> {
                            log.error("Unauthorized for path: {}", request.getRequestURI());
                            response.sendError(401, "Unauthorized");
                        })
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
