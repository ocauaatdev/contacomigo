package com.ocauaatdev.contacomigo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Message> messages;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Transaction> transactions;


}
