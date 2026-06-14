package com.ocauaatdev.contacomigo.dto.message;

import com.ocauaatdev.contacomigo.entity.Sender;

import java.util.UUID;

public record SendMessageDTO(String content, Sender sender, UUID conversationId) {
}
