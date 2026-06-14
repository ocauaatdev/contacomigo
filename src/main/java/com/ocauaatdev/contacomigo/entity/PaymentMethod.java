package com.ocauaatdev.contacomigo.entity;

public enum PaymentMethod {
    PIX,
    CREDIT,
    DEBIT,
    CASH;

    public static PaymentMethod fromString(String method) {
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + method);
        }
    }
}
