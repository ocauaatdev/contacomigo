package com.ocauaatdev.contacomigo.dto.user;

import java.math.BigDecimal;

public record UserCreateResponseDTO(String name, String email, Double balance) {
}
