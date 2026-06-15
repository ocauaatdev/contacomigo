package com.ocauaatdev.contacomigo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record UserCreateDTO(
        @NotBlank(message = "User name cannot be empty")
        String name,

        @NotBlank(message = "User email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{7,}$",
                message = "Password must contain at least 7 characters, one uppercase, one lowercase, one number and one special character"
        )
        String password
) {
}
