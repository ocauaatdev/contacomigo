package com.ocauaatdev.contacomigo.dto.conversation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseConversationDTO(UUID id, String title, LocalDateTime createdAt) {
}
