package com.ocauaatdev.contacomigo.dto;

import java.math.BigDecimal;

public record AiExtractedTransactionDTO(
        BigDecimal amount,
        String description,
        String type,           // "EXPENSE" ou "INCOME"
        String category,       // uma das do seu enum Category
        String paymentMethod,  // uma das do seu enum PaymentMethod, ou null
        String transactionDate    // no formato "yyyy-MM-dd"
) {
}
