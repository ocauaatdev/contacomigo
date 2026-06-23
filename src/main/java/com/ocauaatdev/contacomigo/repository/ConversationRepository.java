package com.ocauaatdev.contacomigo.repository;

import com.ocauaatdev.contacomigo.entity.Conversation;
import com.ocauaatdev.contacomigo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    List<Conversation> findByUserId(UUID id);
}
