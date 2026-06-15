package com.ocauaatdev.contacomigo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
        @NotBlank(message = "User name cannot be empty")
        String name,

        @NotBlank(message = "User email cannot be empty")
        @Email(message = "Invalid email format")
        String email
) {
}
