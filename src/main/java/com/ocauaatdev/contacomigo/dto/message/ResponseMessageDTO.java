package com.ocauaatdev.contacomigo.dto.message;

import com.ocauaatdev.contacomigo.entity.Sender;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseMessageDTO(UUID id, String content, Sender sender, LocalDateTime createdAt) {
}
