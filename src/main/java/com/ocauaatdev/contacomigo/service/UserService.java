package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.user.UpdatePasswordDTO;
import com.ocauaatdev.contacomigo.dto.user.UserCreateDTO;
import com.ocauaatdev.contacomigo.dto.user.UserResponseDTO;
import com.ocauaatdev.contacomigo.dto.user.UserUpdateDTO;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.ForbiddenException;
import com.ocauaatdev.contacomigo.exception.BusinessException;
import com.ocauaatdev.contacomigo.exception.DataAlreadyExistsException;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.UserRepository;
import com.ocauaatdev.contacomigo.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDTO create(UserCreateDTO dto) {

        validateEmailAlreadyExists(dto.email());

        String encryptedPassword = passwordEncoder.encode(dto.password());

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(encryptedPassword);

        User saved = repository.save(user);
        return new UserResponseDTO(saved);
    }

    public UserResponseDTO update(UUID id, UserUpdateDTO dto) {
        validateOwnership(id);
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!user.getEmail().equalsIgnoreCase(dto.email())) {
            validateEmailAlreadyExists(dto.email());
        }

        user.setName(Objects.requireNonNullElse(dto.name(), user.getName()));
        user.setEmail(Objects.requireNonNullElse(dto.email(), user.getEmail()));

        repository.save(user);
        return new UserResponseDTO(user);
    }

    public void delete(UUID id) {
        validateOwnership(id);
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        repository.delete(user);
    }

    public UserResponseDTO getById(UUID id) {
        validateOwnership(id);
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return new UserResponseDTO(user);
    }

    public void updatePassword(UUID id,UpdatePasswordDTO dto) {
        validateOwnership(id);

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect.");
        }

        String encryptedNewPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(encryptedNewPassword);

        repository.save(user);
    }

    private void validateEmailAlreadyExists(String email) {
        if (repository.findByEmailIgnoreCase(email).isPresent()) {
            throw new DataAlreadyExistsException("This email already exists.");
        }
    }

    private void validateOwnership(UUID userId){
        User authenticatedUser = securityUtils.getAuthenticatedUser();
        if (!authenticatedUser.getId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to perform this action.");
        }
    }
}