package org.example.mappro.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.mappro.auth.model.Role;
import org.example.mappro.auth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.example.mappro.exception.ResourceConflictException;
import org.example.mappro.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new ResourceConflictException(
                    "ROLE_NAME_CONFLICT",
                    "Роль '" + role.getName() + "' уже существует"
            );
        }
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "ROLE_NOT_FOUND",
                    "Роль с ID " + id + " не найдена"
            );
        }
        roleRepository.deleteById(id);
    }
}
