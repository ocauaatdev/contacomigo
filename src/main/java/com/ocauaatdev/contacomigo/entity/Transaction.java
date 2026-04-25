package com.ocauaatdev.contacomigo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String type; // transformar em ENUM (EXPENSE, INCOME)
    private BigDecimal amount;
    private String description;
    private String paymentMethod; // transformar em Enum (PIX, CREDIT, DEBIT, CASH)
    private String category; //transformar em ENUM
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;


}
