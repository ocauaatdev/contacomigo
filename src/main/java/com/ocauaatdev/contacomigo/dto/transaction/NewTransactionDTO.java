package com.ocauaatdev.contacomigo.dto.transaction;

import com.ocauaatdev.contacomigo.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record NewTransactionDTO(
        @NotBlank(message = "Description is required.")
        String description,

        @NotNull(message = "Amount is required.")
        @Positive(message = "Amount must be positive.")
        BigDecimal amount,

        @NotNull(message = "Type is required.")
        TypeTransaction type,

        Category category,

        PaymentMethod paymentMethod,

        @NotNull(message = "Transaction date is required.")
        LocalDate transactionDate,

        @NotNull(message = "User is required.")
        UUID idUser,

        @NotNull(message = "Conversation is required.")
        UUID idConversation
) {
}
