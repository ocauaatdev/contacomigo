package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.user.UserCreateDTO;
import com.ocauaatdev.contacomigo.dto.user.UserResponseDTO;
import com.ocauaatdev.contacomigo.dto.user.UserUpdateDTO;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.DataAlreadyExistsException;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserResponseDTO create(UserCreateDTO dto) {

        validateEmailAlreadyExists(dto.email());

        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(encryptedPassword);

        repository.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }

    public UserResponseDTO update(UUID id, UserUpdateDTO dto) {

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!user.getEmail().equalsIgnoreCase(dto.email())) {
            validateEmailAlreadyExists(dto.email());
        }

        user.setName(dto.name());
        user.setEmail(dto.email());

        repository.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }

    private void validateEmailAlreadyExists(String email) {
        if (repository.findByEmailIgnoreCase(email).isPresent()) {
            throw new DataAlreadyExistsException("This email already exists.");
        }
    }
}