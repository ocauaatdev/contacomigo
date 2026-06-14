package com.ocauaatdev.contacomigo.dto.transaction;

import com.ocauaatdev.contacomigo.entity.Category;
import com.ocauaatdev.contacomigo.entity.PaymentMethod;
import com.ocauaatdev.contacomigo.entity.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ResponseTransactionDTO(
        UUID id,
        String description,
        BigDecimal amount,
        TypeTransaction type,
        Category category,
        PaymentMethod paymentMethod,
        LocalDate transactionDate
) {
}
