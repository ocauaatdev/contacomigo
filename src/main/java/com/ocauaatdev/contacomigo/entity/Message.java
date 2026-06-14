package com.ocauaatdev.contacomigo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Sender sender;

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    public Message(String systemResponseText, Sender sender, Conversation conversation, LocalDateTime now) {
        this.content = systemResponseText;
        this.sender = sender;
        this.conversation = conversation;
        this.createdAt = now;
    }
}
