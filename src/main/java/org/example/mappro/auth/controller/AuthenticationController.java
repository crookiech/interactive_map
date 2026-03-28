package org.example.mappro.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.mappro.auth.model.AuthenticationRequest;
import org.example.mappro.auth.model.AuthenticationResponse;
import org.example.mappro.auth.model.User;
import org.example.mappro.response.CustomResponse;
import org.example.mappro.auth.service.JwtUtil;
import org.example.mappro.auth.service.RefreshTokenService;
import org.example.mappro.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v3/auth")
@AllArgsConstructor
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;  // новый сервис

    // ================= LOGIN =================
    @PostMapping("/authenticate")
    public ResponseEntity<CustomResponse<AuthenticationResponse>> createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response) {   // добавляем HttpServletResponse

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            logger.error("Ошибка аутентификации для пользователя: {}", authenticationRequest.getUsername(), e);
            return ResponseEntity.status(401).body(
                    new CustomResponse<>(401, "Неверное имя пользователя или пароль", null)
            );
        }

        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toList());

        final String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), roles);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        // Сохраняем refresh токен в БД (ротация: при логине удаляем старые токены пользователя или просто сохраняем новый)
        // Здесь можно удалить все старые refresh токены пользователя, чтобы осталась только одна активная сессия
        refreshTokenService.removeAllByUsername(userDetails.getUsername()); // опционально
        refreshTokenService.save(userDetails.getUsername(), refreshToken, Duration.ofDays(7));

        // Устанавливаем refresh токен в httpOnly cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)   // в production должно быть true (HTTPS)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Возвращаем access токен (refresh тоже можно вернуть, но он уже в cookie)
        AuthenticationResponse authResponse = new AuthenticationResponse(accessToken, refreshToken);
        return ResponseEntity.ok(new CustomResponse<>(200, "Аутентификация прошла успешно", authResponse));
    }

    // ================= REFRESH =================
    @PostMapping("/refresh")
    public ResponseEntity<CustomResponse<AuthenticationResponse>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).body(
                    new CustomResponse<>(401, "Refresh token отсутствует", null)
            );
        }

        // 1. Проверка JWT
        if (!jwtUtil.validateToken(refreshToken, true)) {
            return ResponseEntity.status(401).body(
                    new CustomResponse<>(401, "Недействительный refresh токен", null)
            );
        }

        String username = jwtUtil.extractUsername(refreshToken, true);

        // 2. Проверка наличия в БД и что не отозван
        if (!refreshTokenService.isValid(username, refreshToken)) {
            return ResponseEntity.status(401).body(
                    new CustomResponse<>(401, "Refresh токен отозван или не найден", null)
            );
        }

        // 3. Загружаем пользователя и генерируем новую пару
        UserDetails userDetails = userService.loadUserByUsername(username);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toList());

        String newAccessToken = jwtUtil.generateAccessToken(username, roles);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        // 4. Ротация: удаляем старый refresh токен, сохраняем новый
        refreshTokenService.rotate(username, refreshToken, newRefreshToken, Duration.ofDays(7));

        // 5. Устанавливаем новый refresh токен в cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 6. Возвращаем новый access токен
        AuthenticationResponse authResponse = new AuthenticationResponse(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(new CustomResponse<>(200, "Токены обновлены", authResponse));
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<CustomResponse<String>> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok(
                    new CustomResponse<>(200, "Пользователь успешно зарегистрирован", null)
            );
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка регистрации пользователя: {}", user.getUsername(), e);
            return ResponseEntity.badRequest().body(
                    new CustomResponse<>(400, e.getMessage(), null)
            );
        }
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public ResponseEntity<CustomResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null) {
            try {
                String username = jwtUtil.extractUsername(refreshToken, true);
                refreshTokenService.removeByUsernameAndToken(username, refreshToken);
            } catch (Exception e) {
                // логируем, но не мешаем выходу
                logger.warn("Ошибка при удалении refresh токена: {}", e.getMessage());
            }
        }

        // Очищаем cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new CustomResponse<>(200, "Выход выполнен", null));
    }
}
