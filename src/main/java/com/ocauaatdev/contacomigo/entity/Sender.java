package com.ocauaatdev.contacomigo.entity;

public enum Sender {
    USER,
    ASSISTANT;

    public static Sender fromString(String sender) {
        try {
            return Sender.valueOf(sender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sender: " + sender);
        }
    }
}
