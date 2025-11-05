package com.level_up.usuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioSaveException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioSaveException(UsuarioSaveException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error al guardar el usuario");
        respuesta.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    @ExceptionHandler(UsuarioUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioUpdateException(UsuarioUpdateException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error al actualizar el usuario");
        respuesta.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    @ExceptionHandler(UsuarioLoginException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioLoginException(UsuarioLoginException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error al iniciar sesion");
        respuesta.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNotFound(UsuarioNotFoundException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error de validación en los campos enviados");
        respuesta.put("errores", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }
}
