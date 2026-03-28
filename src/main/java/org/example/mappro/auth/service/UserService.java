package org.example.mappro.auth.service;

import org.example.mappro.auth.dto.UserDto;
import org.example.mappro.auth.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    void registerUser(User user);
    UserDetails loadUserByUsername(String username);
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto create(UserDto userDto);
    UserDto update(UserDto userDto);
    void delete(Long id);
    UserDto findByUsername(String username);
    UserDto updatePassword(Long id, String newPassword);
    UserDto updateEmail(Long id, String newEmail);
    UserDto updateRole(Long id, Long roleId);
}