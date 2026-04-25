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

    private String sender; //transformar em Enum (USER / SYSTEM)

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;


}
