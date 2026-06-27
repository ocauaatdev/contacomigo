package com.ocauaatdev.contacomigo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
        String name,

        @Email(message = "Invalid email format")
        String email
) {
}
