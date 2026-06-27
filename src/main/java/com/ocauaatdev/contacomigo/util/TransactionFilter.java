package com.ocauaatdev.contacomigo.util;


import com.ocauaatdev.contacomigo.entity.Category;
import com.ocauaatdev.contacomigo.entity.PaymentMethod;
import com.ocauaatdev.contacomigo.entity.TypeTransaction;

import java.time.LocalDate;

public record TransactionFilter(
        LocalDate startDate,
        LocalDate endDate,
        Category category,
        TypeTransaction type,
        PaymentMethod paymentMethod
) {
}
