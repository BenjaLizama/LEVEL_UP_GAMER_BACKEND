package com.level_up.carrito.controller;

import com.level_up.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/carritos")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "Controlador publico de carrito", description = "Define metodos publicos del carrito")
public class PublicCarritoController {

    private final CarritoService carritoService;

    // ✅ Inicializar carrito
    @Operation(summary = "Inicializa un carrito vacoo para un nuevo usuario (Llamado por MS Usuarios)")
    @ApiResponse(responseCode = "200", description = "Carrito inicializado con exito")
    @PostMapping("/inicializar/{idUsuario}")
    public ResponseEntity<Void> inicializarCarrito(@PathVariable("idUsuario") Long idUsuario) {
        carritoService.inicializarCarrito(idUsuario);
        return ResponseEntity.ok().build();
    }
}
