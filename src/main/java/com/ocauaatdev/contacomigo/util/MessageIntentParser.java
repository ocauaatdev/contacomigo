package com.ocauaatdev.contacomigo.util;

import com.ocauaatdev.contacomigo.entity.MessageIntent;

import java.util.List;
import java.util.regex.Pattern;

public class MessageIntentParser {
    
        // Padrão já existente no TransactionParser
        private static final Pattern REGISTER_PATTERN = Pattern.compile(
                "^(gastei|ganhei)\\s+.+",
                Pattern.CASE_INSENSITIVE
        );

        // Palavras que indicam que o usuário quer ver informações
        private static final List<String> EXTRACT_KEYWORDS = List.of(
                "extrato", "gastos", "ganhos", "lançamentos", "lancamentos",
                "transações", "transacoes", "historico", "histórico",
                "ver meus", "mostrar", "quero ver", "me mostre"
        );

        public static MessageIntent detect(String text) {
            if (text == null || text.isBlank()) {
                return MessageIntent.UNKNOWN;
            }

            String normalized = text.trim().toLowerCase();

            // Verifica registro primeiro — tem padrão mais específico
            if (REGISTER_PATTERN.matcher(normalized).matches()) {
                return MessageIntent.REGISTER_TRANSACTION;
            }

            // Verifica se é consulta de extrato
            if (EXTRACT_KEYWORDS.stream().anyMatch(normalized::contains)) {
                return MessageIntent.QUERY_EXTRACT;
            }

            return MessageIntent.UNKNOWN;
        }
}
