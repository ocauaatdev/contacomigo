package com.ocauaatdev.contacomigo.dto.message;

public record MessageInteractionDTO(
        ResponseMessageDTO userMessage,
        ResponseMessageDTO assistantMessage
) {
}
