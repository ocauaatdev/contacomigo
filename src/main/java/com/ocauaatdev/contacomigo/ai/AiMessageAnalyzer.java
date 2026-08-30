package com.ocauaatdev.contacomigo.ai;

import com.ocauaatdev.contacomigo.dto.AiMessageAnalysisDTO;

import java.util.Optional;

public interface AiMessageAnalyzer {
    Optional<AiMessageAnalysisDTO> analyze(String userMessage);
}
