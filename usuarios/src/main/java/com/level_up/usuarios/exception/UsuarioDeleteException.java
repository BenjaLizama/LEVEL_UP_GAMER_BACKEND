package com.level_up.usuarios.exception;

public class UsuarioDeleteException extends RuntimeException {

    public UsuarioDeleteException(String mensaje) {
        super(mensaje);
    }

    public UsuarioDeleteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
