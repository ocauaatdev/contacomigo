package com.ocauaatdev.contacomigo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordDTO(
        @NotBlank
        String currentPassword,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{7,}$",
                message = "Password must contain at least 7 characters, one uppercase, one lowercase, one number and one special character"
        )
        String newPassword
) {
}
