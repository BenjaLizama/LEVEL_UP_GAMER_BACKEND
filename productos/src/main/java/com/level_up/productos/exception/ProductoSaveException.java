package com.level_up.productos.exception;

public class ProductoSaveException extends RuntimeException {

    public ProductoSaveException(String mensaje) {
        super(mensaje);
    }

    public ProductoSaveException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
