package org.example.mappro.auth.dto;

import org.example.mappro.auth.model.Role;

public record UserDto(
        Long id,
        String username,
        String email,
        Role role
) {}
