package org.example.mappro.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.mappro.auth.dto.UserDto;
import org.example.mappro.auth.service.UserService;
import org.example.mappro.exception.RequestValidationException;
import org.example.mappro.response.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Список пользователей успешно получен",
                users
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Пользователь найден", user);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody UserDto userDto) {
        UserDto updated = userService.update(userDto);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Пользователь обновлён", updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Пользователь успешно удалён", null);
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        UserDto user = userService.findByUsername(username);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Пользователь найден", user);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String password = requireText(body.get("password"), "password");
        UserDto updated = userService.updatePassword(id, password);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Пароль успешно обновлён", updated);
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<Map<String, Object>> updateEmail(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String email = requireText(body.get("email"), "email");
        UserDto updated = userService.updateEmail(id, email);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Email успешно обновлён", updated);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body
    ) {
        Long roleId = body.get("roleId");
        if (roleId == null) {
            throw new RequestValidationException(
                    "REQUIRED_FIELD_MISSING",
                    "Поле roleId обязательно"
            );
        }
        UserDto updated = userService.updateRole(id, roleId);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Роль успешно обновлена", updated);
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new RequestValidationException(
                    "REQUIRED_FIELD_MISSING",
                    "Поле " + field + " обязательно"
            );
        }
        return value;
    }
}
