package com.ocauaatdev.contacomigo.dto.user;

import com.ocauaatdev.contacomigo.entity.User;

import java.util.UUID;

public record UserResponseDTO(UUID id, String name, String email) {

    public UserResponseDTO (User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}
