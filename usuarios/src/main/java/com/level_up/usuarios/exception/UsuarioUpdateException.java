package com.level_up.usuarios.exception;

public class UsuarioUpdateException extends RuntimeException {

    public UsuarioUpdateException(String mensaje) {
        super(mensaje);
    }

    public UsuarioUpdateException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
