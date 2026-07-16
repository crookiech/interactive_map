package org.example.mappro.auth.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mappro.config.PublicEndpointsConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthErrorResponseWriter errorWriter;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService,
                            AuthErrorResponseWriter errorWriter) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.errorWriter = errorWriter;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") || 
               path.startsWith("/swagger-resources") || 
               path.startsWith("/webjars") ||
               path.startsWith("/api-docs") ||
               path.equals("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (isPublicEndpoint(request)) {
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token, false)) {
                try {
                    String username = jwtUtil.extractUsername(token, false);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    List<SimpleGrantedAuthority> authorities = jwtUtil.extractRoles(token).stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } catch (RuntimeException exception) {
                    SecurityContextHolder.clearContext();
                    errorWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Недействительный access token");
                    return;
                }
            } else {
                errorWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Недействительный access token");
                return;
            }
        } else {
            errorWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Требуется заголовок Authorization");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            for (String endpoint : PublicEndpointsConfig.PUBLIC_GET_ENDPOINTS) {
                if (pathMatcher.match(endpoint, uri)) return true;
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            for (String endpoint : PublicEndpointsConfig.PUBLIC_POST_ENDPOINTS) {
                if (pathMatcher.match(endpoint, uri)) return true;
            }
        }

        return false;
    }
}
