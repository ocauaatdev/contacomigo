package com.ocauaatdev.contacomigo.dto.conversation;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NewconversationDTO(
        String title,

        @NotNull
        UUID idUser
) {
}
