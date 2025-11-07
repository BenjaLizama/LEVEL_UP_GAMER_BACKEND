package com.level_up.productos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductoSaveException.class)
    public ResponseEntity<Map<String, Object>> handleProductoSaveException(ProductoSaveException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error al guardar el producto");
        respuesta.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductoNotFoundException(ProductoNotFoundException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error al encontrar el producto");
        respuesta.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(ProductoDeleteException.class)
    public ResponseEntity<Map<String, Object>> handleProductoDeleteException(ProductoDeleteException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error al eliminar el producto");
        respuesta.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error interno del servidor");
        respuesta.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }

    @ExceptionHandler(ListaProductosException.class)
    public ResponseEntity<Map<String, Object>> handleListaProductosException(ListaProductosException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "No se encontraron productos");
        respuesta.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nombreCampo = ((FieldError) error).getField();
            String mensajeError = error.getDefaultMessage();
            errores.put(nombreCampo, mensajeError);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }
}
