package com.ocauaatdev.contacomigo.dto.user;

import java.util.UUID;

public record UserResponseDTO(UUID id, String name, String email) {
}
