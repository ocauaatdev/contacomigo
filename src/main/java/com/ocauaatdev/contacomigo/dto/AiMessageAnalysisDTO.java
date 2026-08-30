package com.ocauaatdev.contacomigo.dto;

import java.math.BigDecimal;

public record AiMessageAnalysisDTO(
        String intent, // "REGISTER_TRANSACTION", "QUERY_EXTRACT" ou "UNKNOWN"

        // Preenchidos SOMENTE se intent = REGISTER_TRANSACTION
        BigDecimal amount,
        String description,
        String type,
        String category,
        String paymentMethod,
        String transactionDate,

        // Preenchidos SOMENTE se intent = QUERY_EXTRACT, e cada um s se o
        // usurio de fato mencionou aquele filtro (seno, null = "sem filtro")
        String filterStartDate,
        String filterEndDate,
        String filterCategory,
        String filterType,
        String filterPaymentMethod
) {
}
