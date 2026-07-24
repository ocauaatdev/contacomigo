package com.ocauaatdev.contacomigo.dto.message;

import com.ocauaatdev.contacomigo.dto.transaction.ResponseTransactionDTO;

import java.util.List;
import java.util.UUID;

public record MessageInteractionDTO(
        ResponseMessageDTO userMessage,
        ResponseMessageDTO assistantMessage,
        UUID transactionId,
        List<ResponseTransactionDTO> transactions
) {
}
