package org.example.mappro.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.mappro.auth.model.Role;
import org.example.mappro.auth.service.RoleService;
import org.example.mappro.response.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v3/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<CustomResponse<Role>> createRole(@RequestBody Role role) {
        Role created = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResponse<>(201, "Роль успешно создана", created));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(
                new CustomResponse<>(200, "Список ролей успешно получен", roles)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(
                new CustomResponse<>(200, "Роль успешно удалена", null)
        );
    }
}
