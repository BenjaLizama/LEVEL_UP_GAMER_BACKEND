package com.level_up.usuarios.exception;

public class UsuarioException extends RuntimeException {

    public UsuarioException(String mensaje) {
        super(mensaje);
    }

    public UsuarioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
