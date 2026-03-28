package org.example.mappro.auth.controller;

import lombok.AllArgsConstructor;
import org.example.mappro.auth.dto.UserDto;
import org.example.mappro.response.ResponseBuilder;
import org.example.mappro.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /* ===================== GET ===================== */

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Список пользователей успешно получен", users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.findById(id);
            return ResponseBuilder.buildResponse(HttpStatus.OK, "Пользователь найден", user);
        } catch (IllegalArgumentException e) {
            logger.error("Пользователь не найден с ID: {}", id, e);
            return ResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /* ===================== UPDATE ===================== */

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody UserDto userDto) {
        try {
            UserDto updated = userService.update(userDto);
            return ResponseBuilder.buildResponse(HttpStatus.OK, "Пользователь обновлён", updated);
        } catch (IllegalArgumentException e) {
            logger.error("Не удалось обновить пользователя с ID: {}", userDto.id(), e);
            return ResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя", e);
            return ResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось обновить пользователя");
        }
    }

    /* ===================== DELETE ===================== */

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseBuilder.buildResponse(HttpStatus.OK, "Пользователь успешно удалён", null);
        } catch (IllegalArgumentException e) {
            logger.error("Не удалось удалить пользователя с ID: {}", id, e);
            return ResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя", e);
            return ResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось удалить пользователя");
        }
    }

    /* ===================== GET by username ===================== */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        try {
            UserDto user = userService.findByUsername(username);
            if (user == null) {
                throw new IllegalArgumentException("Пользователь не найден с username: " + username);
            }
            return ResponseBuilder.buildResponse(HttpStatus.OK, "Пользователь найден", user);
        } catch (IllegalArgumentException e) {
            logger.error("Пользователь не найден с username: {}", username, e);
            return ResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при получении пользователя с username: {}", username, e);
            return ResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось получить пользователя");
        }
    }

    /* ===================== PATCH ===================== */

    // Изменение пароля
    @PatchMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String newPassword = body.get("password");
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseBuilder.buildErrorResponse(HttpStatus.BAD_REQUEST, "Пароль не может быть пустым");
            }
            UserDto updated = userService.updatePassword(id, newPassword);
            return ResponseBuilder.buildResponse(HttpStatus.OK, "Пароль успешно обновлён", updated);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось обновить пароль");
        }
    }

    // Изменение email
    @PatchMapping("/{id}/email")
    public ResponseEntity<Map<String, Object>> updateEmail(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String newEmail = body.get("email");
            if (newEmail == null || newEmail.isEmpty()) {
                return ResponseBuilder.buildErrorResponse(HttpStatus.BAD_REQUEST, "Email не может быть пустым");
            }
            UserDto updated = userService.updateEmail(id, newEmail);
            return ResponseBuilder.buildResponse(HttpStatus.OK, "Email успешно обновлён", updated);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось обновить email");
        }
    }

    // Изменение роли пользователя
    @PatchMapping("/{id}/role")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body   // или Map<String, Object> и преобразование
    ) {
        try {
            Long roleId = body.get("roleId");
            if (roleId == null) {
                return ResponseBuilder.buildErrorResponse(HttpStatus.BAD_REQUEST, "roleId не может быть пустым");
            }
            UserDto updated = userService.updateRole(id, roleId);
            return ResponseBuilder.buildResponse(HttpStatus.OK, "Роль успешно обновлена", updated);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось обновить роль");
        }
    }

}
