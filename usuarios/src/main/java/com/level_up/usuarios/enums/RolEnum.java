package com.level_up.usuarios.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // Crea el método getNombre() o getValue() automáticamente
@AllArgsConstructor // Crea el constructor privado que asigna el valor
public enum RolEnum {
    USER("USER"),
    ADMIN("ADMIN");

    // Este es el campo que te faltaba
    private final String value;
}