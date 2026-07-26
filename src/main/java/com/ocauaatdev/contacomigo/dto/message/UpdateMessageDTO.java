package com.ocauaatdev.contacomigo.dto.message;

import jakarta.validation.constraints.NotBlank;

public record UpdateMessageDTO(
        @NotBlank(message = "Content is required.")
        String content
) {}
