package com.level_up.productos.exception;

public class ListaProductosException extends RuntimeException {

    public ListaProductosException(String mensaje) {
        super(mensaje);
    }

    public ListaProductosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
