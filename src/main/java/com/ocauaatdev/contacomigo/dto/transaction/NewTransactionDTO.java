package com.ocauaatdev.contacomigo.dto.transaction;

import com.ocauaatdev.contacomigo.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NewTransactionDTO(
        String description,
        BigDecimal amount,
        TypeTransaction type,
        Category category,
        PaymentMethod paymentMethod,
        LocalDate transactionDate,
        User user,
        Conversation conversation
) {
}
