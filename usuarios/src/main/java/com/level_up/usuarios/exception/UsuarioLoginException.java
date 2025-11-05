package com.level_up.usuarios.exception;

public class UsuarioLoginException extends RuntimeException {

    public UsuarioLoginException(String mensaje) {
        super(mensaje);
    }

    public UsuarioLoginException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
