package com.level_up.productos.controller;

import com.level_up.productos.dto.ProductoDTO;
import com.level_up.productos.dto.ProductoRetornoDTO;
import com.level_up.productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Gestion de productos", description = "Endpoints para gestionar los productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Agregar producto")
    @ApiResponse(responseCode = "201", description = "Se creo el recurso")
    @ApiResponse(responseCode = "409", description = "Conflicto al intentar crear el recurso")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PostMapping
    public ResponseEntity<ProductoRetornoDTO> agregarProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        ProductoRetornoDTO producto = productoService.agregarProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }


}
