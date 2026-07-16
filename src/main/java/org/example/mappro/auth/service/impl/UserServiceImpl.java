package org.example.mappro.auth.service.impl;

import org.example.mappro.auth.dto.UserDto;
import org.example.mappro.auth.model.Role;
import org.example.mappro.auth.model.User;
import org.example.mappro.auth.repository.RoleRepository;
import org.example.mappro.auth.repository.UserRepository;
import org.example.mappro.auth.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName())));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return mapToDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.username())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode("default123")); // временный пароль
        user.setRole(userDto.role());
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepository.findById(userDto.id())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setRole(userDto.role());
        return mapToDto(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден с username: " + username));
        return mapToDto(user);
    }

    @Override
    public UserDto updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setPassword(passwordEncoder.encode(newPassword));
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateEmail(Long id, String newEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setEmail(newEmail);
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateRole(Long userId, Long roleId) {
        // Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Находим новую роль
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Роль не найдена"));

        // Устанавливаем новую роль
        user.setRole(role);

        // Сохраняем и возвращаем DTO
        return mapToDto(userRepository.save(user));
    }


    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}