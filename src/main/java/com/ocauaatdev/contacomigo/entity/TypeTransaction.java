package com.ocauaatdev.contacomigo.entity;

public enum TypeTransaction {
    EXPENSE,
    INCOME;

    public static TypeTransaction fromString(String type) {
        try {
            return TypeTransaction.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }
}
