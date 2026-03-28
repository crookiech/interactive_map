package org.example.mappro.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.mappro.auth.model.Role;
import org.example.mappro.auth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Роль '" + role.getName() + "' уже существует");
        }
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Роль с ID " + id + " не найдена");
        }
        roleRepository.deleteById(id);
    }
}
