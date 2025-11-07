package com.level_up.productos.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CategoriaEnum {
    JUEGO_MESA("JM"),
    ACCESORIOS("ACC"),
    CONSOLAS("CON"),
    COMPUTADORES_GAMERS("CG"),
    SILLAS_GAMERS("SG"),
    MOUSE("MO"),
    MOUSE_PAD("MP"),
    POLERAS_PERSONALIZADAS("PP"),
    POLERONES_GAMERS_PERSONALIZADOS("PGP");

    private final String prefijo;

    public String getPrefijo() {
        return prefijo;
    }
}
