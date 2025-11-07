package com.level_up.productos.exception;

public class ProductoDeleteException extends RuntimeException {

    public ProductoDeleteException(String mensaje) {
        super(mensaje);
    }

    public ProductoDeleteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
