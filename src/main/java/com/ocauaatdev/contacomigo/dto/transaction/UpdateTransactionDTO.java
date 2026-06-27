package com.ocauaatdev.contacomigo.dto.transaction;

import com.ocauaatdev.contacomigo.entity.Category;
import com.ocauaatdev.contacomigo.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionDTO(
        String description,

        @Positive(message = "Amount must be positive.")
        BigDecimal amount,

        Category category,

        PaymentMethod paymentMethod,

        LocalDate transactionDate
) {
}
