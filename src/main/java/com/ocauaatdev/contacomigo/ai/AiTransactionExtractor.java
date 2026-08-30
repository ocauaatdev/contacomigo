package com.ocauaatdev.contacomigo.ai;

import com.ocauaatdev.contacomigo.dto.AiExtractedTransactionDTO;

import java.util.Optional;

public interface AiTransactionExtractor {
    Optional<AiExtractedTransactionDTO> extract(String userMessage);
}
