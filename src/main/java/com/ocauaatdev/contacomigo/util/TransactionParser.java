package com.ocauaatdev.contacomigo.util;

import com.ocauaatdev.contacomigo.entity.Category;
import com.ocauaatdev.contacomigo.entity.PaymentMethod;
import com.ocauaatdev.contacomigo.entity.TypeTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionParser {

    private static final Pattern BASE_PATTERN = Pattern.compile(
            "^(gastei|ganhei)\\s+(\\d+(?:[.,]\\d+)*)\\s+(.+)$",
            Pattern.CASE_INSENSITIVE
    );

    public record ParsedData(
            BigDecimal amount,
            String description,
            TypeTransaction type,
            Category category,
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
            BigDecimal amount = normalizeAmount(matcher.group(2));
            String remainderText = matcher.group(3).trim();

            TypeTransaction type = command.equals("gastei") ? TypeTransaction.EXPENSE : TypeTransaction.INCOME;

            // 1. LIMPEZA DE MOEDA: Remove "reais", "real", "R$" logo de cara
            remainderText = remainderText.replaceAll("(?i)\\b(reais|real|r\\$)\\b", "").trim();

            // 2. EXTRAIR FORMA DE PAGAMENTO
            PaymentMethod paymentMethod = extractPaymentMethod(remainderText);
            if (paymentMethod != null) {
                remainderText = remainderText.replaceAll("(?i)\\b(no |na |em |com )?(credito|crédito|debito|débito|pix|dinheiro)\\b", "").trim();
            }

            // 3. EXTRAIR DATA
            LocalDate transactionDate = extractDate(remainderText);
            remainderText = remainderText.replaceAll("(?i)\\b(ontem|hoje|semana passada|mes passado|mês passado)\\b", "").trim();

            // 4. LIMPAR DESCRIÇÃO FINAL
            String description = cleanDescription(remainderText);

            Category category = Category.OTHER;

            return new ParsedData(amount, description, type, category, paymentMethod, transactionDate);
        }

        return null;
    }

    /**
     * Converte um valor monetário em formato pt-BR (ou simplificado) para BigDecimal.
     * Exemplos suportados:
     *  "1500"         -> 1500.00
     *  "35,90"        -> 35.90
     *  "35.90"        -> 35.90   (1-2 dígitos após o ponto = decimal)
     *  "1.500"        -> 1500.00 (3 dígitos após o ponto = milhar)
     *  "1.500,00"     -> 1500.00
     *  "1.000.000,00" -> 1000000.00
     */
    private static BigDecimal normalizeAmount(String raw) {
        boolean hasComma = raw.contains(",");
        boolean hasDot = raw.contains(".");

        String normalized;

        if (hasComma && hasDot) {
            // "." = milhar, "," = decimal
            normalized = raw.replace(".", "").replace(",", ".");
        } else if (hasComma) {
            // "," é o decimal
            normalized = raw.replace(",", ".");
        } else if (hasDot) {
            int lastDot = raw.lastIndexOf('.');
            String afterDot = raw.substring(lastDot + 1);
            // 3 dígitos após o último ponto -> separador de milhar
            normalized = (afterDot.length() == 3)
                    ? raw.replace(".", "")
                    : raw;
        } else {
            normalized = raw;
        }

        return new BigDecimal(normalized).setScale(2, RoundingMode.HALF_UP);
    }

    private static PaymentMethod extractPaymentMethod(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("debito") || lower.contains("débito")) return PaymentMethod.DEBIT;
        if (lower.contains("credito") || lower.contains("crédito")) return PaymentMethod.CREDIT;
        if (lower.contains("pix")) return PaymentMethod.PIX;
        if (lower.contains("dinheiro")) return PaymentMethod.CASH;
        return null;
    }

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
        text = text.replaceAll("\\s+", " ").trim();
        String cleaned = text.replaceAll("(?i)^\\b(no|na|com|em|de)\\b\\s*", "").trim();

        if (cleaned.isBlank()) {
            return "Outros";
        }
        return cleaned.substring(0, 1).toUpperCase() + cleaned.substring(1);
    }
}