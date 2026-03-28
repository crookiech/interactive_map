package org.example.mappro.auth.controller;

import lombok.AllArgsConstructor;
import org.example.mappro.auth.model.Role;
import org.example.mappro.response.CustomResponse;
import org.example.mappro.auth.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v3/roles")
@AllArgsConstructor
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;

    /* ===================== CREATE ROLE ===================== */

    @PostMapping
    public ResponseEntity<CustomResponse<Role>> createRole(@RequestBody Role role) {
        try {
            Role created = roleService.createRole(role);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomResponse<>(201, "Роль успешно создана", created));
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при создании роли: {}", role.getName(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse<>(400, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при создании роли", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(500, "Ошибка при создании роли", null));
        }
    }

    /* ===================== GET ALL ROLES ===================== */

    @GetMapping
    public ResponseEntity<CustomResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(new CustomResponse<>(200, "Список ролей успешно получен", roles));
    }

    /* ===================== DELETE ROLE ===================== */

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Void>> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(new CustomResponse<>(200, "Роль успешно удалена", null));
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при удалении роли с ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomResponse<>(404, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Ошибка при удалении роли с ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(500, "Ошибка при удалении роли", null));
        }
    }
}