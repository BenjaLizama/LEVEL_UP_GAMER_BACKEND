package com.level_up.usuarios.exception;

public class UsuarioSaveException extends RuntimeException {

    public UsuarioSaveException(String mensaje) {
        super(mensaje);
    }

    public UsuarioSaveException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
