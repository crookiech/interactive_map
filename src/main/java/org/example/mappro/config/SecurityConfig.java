package org.example.mappro.config;

import lombok.extern.slf4j.Slf4j;
import org.example.mappro.auth.service.JwtRequestFilter;
import org.example.mappro.auth.service.AuthErrorResponseWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    private final AuthErrorResponseWriter errorWriter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, AuthErrorResponseWriter errorWriter) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.errorWriter = errorWriter;
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
        config.setAllowedOrigins(List.of("http://localhost:8081", "https://bug-tracker-gtt.ru/"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/v3/api-docs",
                "/swagger-resources/**",
                "/webjars/**",
                "/api-docs/**"
            )
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable());
        
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, PublicEndpointsConfig.PUBLIC_GET_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.POST, PublicEndpointsConfig.PUBLIC_POST_ENDPOINTS).permitAll()
                .requestMatchers(RoleEndpointsConfig.ADMIN_ENDPOINTS).hasRole("ADMIN")
                .requestMatchers(RoleEndpointsConfig.DEVELOPER_ENDPOINTS).hasAnyRole("ADMIN", "DEVELOPER")
                .requestMatchers(RoleEndpointsConfig.USER_ENDPOINTS).hasAnyRole("USER", "ADMIN", "DEVELOPER")
                .requestMatchers("/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(handling -> handling
                .accessDeniedHandler((request, response, ex) -> {
                    log.error("Access denied for path: {}", request.getRequestURI());
                    errorWriter.write(response, 403, "Недостаточно прав");
                })
                .authenticationEntryPoint((request, response, ex) -> {
                    log.error("Unauthorized for path: {}", request.getRequestURI());
                    errorWriter.write(response, 401, "Требуется авторизация");
                })
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
