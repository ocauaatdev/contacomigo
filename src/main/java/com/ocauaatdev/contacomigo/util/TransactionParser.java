package com.ocauaatdev.contacomigo.util;

import com.ocauaatdev.contacomigo.entity.PaymentMethod;
import com.ocauaatdev.contacomigo.entity.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionParser {

    private static final Pattern BASE_PATTERN = Pattern.compile(
            "^(gastei|ganhei)\\s+(\\d+(?:[.,]\\d{2})?)\\s+(.+)$",
            Pattern.CASE_INSENSITIVE
    );

    // ALTERADO: transactionDate agora é LocalDate (Apenas a data)
    public record ParsedData(
            BigDecimal amount,
            String description,
            TypeTransaction type,
            PaymentMethod paymentMethod,
            LocalDate transactionDate
    ) {}

    public static ParsedData parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        Matcher matcher = BASE_PATTERN.matcher(text.trim());

        if (matcher.matches()) {
            String command = matcher.group(1).toLowerCase();
            String rawAmount = matcher.group(2).replace(",", ".");
            String remainderText = matcher.group(3).trim();

            BigDecimal amount = new BigDecimal(rawAmount);
            TypeTransaction type = command.equals("gastei") ? TypeTransaction.EXPENSE : TypeTransaction.INCOME;

            // 1. LIMPEZA DE MOEDA: Remove "reais", "real", "R$" logo de cara
            remainderText = remainderText.replaceAll("(?i)\\b(reais|real|r\\$)\\b", "").trim();

            // 2. EXTRAIR FORMA DE PAGAMENTO
            PaymentMethod paymentMethod = extractPaymentMethod(remainderText);
            if (paymentMethod != null) {
                // Remove a palavra chave E a possível preposição antes dela (ex: "no debito")
                remainderText = remainderText.replaceAll("(?i)\\b(no |na |em |com )?(credito|crédito|debito|débito|pix|dinheiro)\\b", "").trim();
            }

            // 3. EXTRAIR DATA
            LocalDate transactionDate = extractDate(remainderText);
            // Simplesmente remove as palavras de tempo (sem precisar do IF falho)
            remainderText = remainderText.replaceAll("(?i)\\b(ontem|hoje|semana passada|mes passado|mês passado)\\b", "").trim();

            // 4. LIMPAR DESCRIÇÃO FINAL
            String description = cleanDescription(remainderText);

            return new ParsedData(amount, description, type, paymentMethod, transactionDate);
        }

        return null;
    }

    private static PaymentMethod extractPaymentMethod(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("debito") || lower.contains("débito")) return PaymentMethod.DEBIT;
        if (lower.contains("credito") || lower.contains("crédito")) return PaymentMethod.CREDIT;
        if (lower.contains("pix")) return PaymentMethod.PIX;
        if (lower.contains("dinheiro")) return PaymentMethod.CASH;
        return null;
    }

    // ALTERADO: Agora retorna LocalDate
    private static LocalDate extractDate(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("ontem")) {
            return LocalDate.now().minusDays(1);
        }
        if (lower.contains("semana passada")) {
            return LocalDate.now().minusWeeks(1);
        }
        if (lower.contains("mes passado") || lower.contains("mês passado")) {
            return LocalDate.now().minusMonths(1);
        }
        return LocalDate.now();
    }

    private static String cleanDescription(String text) {
        // Garante que não fiquem múltiplos espaços em branco perdidos no meio da frase
        text = text.replaceAll("\\s+", " ").trim();

        // Remove conectores órfãos no INÍCIO da frase final
        String cleaned = text.replaceAll("(?i)^\\b(no|na|com|em|de)\\b\\s*", "").trim();

        if (cleaned.isBlank()) {
            return "Outros";
        }
        return cleaned.substring(0, 1).toUpperCase() + cleaned.substring(1);
    }
}