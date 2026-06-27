package com.ocauaatdev.contacomigo.dto.transaction;

import com.ocauaatdev.contacomigo.entity.Category;
import com.ocauaatdev.contacomigo.entity.PaymentMethod;
import com.ocauaatdev.contacomigo.entity.Transaction;
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

    //construtor que permite receber uma Transaction e ja ajusta os atributos de acordo com o DTO
    public ResponseTransactionDTO (Transaction transaction) {
        this(transaction.getId(), transaction.getDescription(), transaction.getAmount(), transaction.getType(), transaction.getCategory(), transaction.getPaymentMethod(), transaction.getTransactionDate());
    }
}
