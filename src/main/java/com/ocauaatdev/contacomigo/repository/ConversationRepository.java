package com.ocauaatdev.contacomigo.repository;

import com.ocauaatdev.contacomigo.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
