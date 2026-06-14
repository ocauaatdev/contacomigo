package com.ocauaatdev.contacomigo.entity;

public enum Category {
    FOOD,
    TRANSPORT,
    ENTERTAINMENT,
    HEALTH,
    EDUCATION,
    UTILITIES,
    OTHER;

    public static Category fromString(String category) {
        try {
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
    }
}
