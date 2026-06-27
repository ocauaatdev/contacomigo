package com.ocauaatdev.contacomigo.dto.message;

import com.ocauaatdev.contacomigo.entity.Sender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendMessageDTO(
        @NotBlank(message = "Content is required.")
        String content
){
}
