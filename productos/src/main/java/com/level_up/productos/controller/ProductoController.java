package com.level_up.productos.controller;

import com.level_up.productos.dto.ProductoDTO;
import com.level_up.productos.dto.ProductoRetornoDTO;
import com.level_up.productos.enums.CategoriaEnum;
import com.level_up.productos.model.ProductoModel;
import com.level_up.productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Gestion de productos", description = "Endpoints para gestionar los productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // 🔒 Agregar producto
    @Operation(summary = "Agregar producto")
    @ApiResponse(responseCode = "201", description = "Se creo el recurso")
    @ApiResponse(responseCode = "409", description = "Conflicto al intentar crear el recurso")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PostMapping
    public ResponseEntity<ProductoRetornoDTO> agregarProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        ProductoRetornoDTO producto = productoService.agregarProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    // 🔒 Eliminar producto
    @ApiResponse(responseCode = "204", description = "Se elimino el recurso")
    @ApiResponse(responseCode = "409", description = "Conflicto al intentar eliminar el recurso")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @DeleteMapping("/delete/{codigoProducto}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable("codigoProducto") String codigoProducto) {
        productoService.eliminarProducto(codigoProducto);
        return ResponseEntity.noContent().build();
    }

    // ✅ Obtener todos los productos
    @Operation(summary = "Obtener todos los productos")
    @ApiResponse(responseCode = "200", description = "Se obtuvo el recurso correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontro el recurso")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping
    public ResponseEntity<List<ProductoRetornoDTO>> obtenerTodosLosProductos() {
        List<ProductoRetornoDTO> lista_productos = productoService.findAll();
        return ResponseEntity.ok(lista_productos);
    }

    // ✅ Buscar producto con ID
    @Operation(summary = "Buscar producto por ID")
    @ApiResponse(responseCode = "200", description = "Se obtuvo el recurso correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontro el recurso")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoRetornoDTO> encontrarProductoPorId(@PathVariable("idProducto") Long idProducto) {
        ProductoRetornoDTO productoEncontrado = productoService.findById(idProducto);
        return ResponseEntity.ok(productoEncontrado);
    }

    // ✅ Buscar producto con CODIGO
    @Operation(summary = "Buscar producto por CODIGO")
    @ApiResponse(responseCode = "200", description = "Se obtuvo el recurso correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontro el recurso")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/code/{codigoProducto}")
    public ResponseEntity<ProductoRetornoDTO> encontrarProductoPorCodigo(@PathVariable("codigoProducto") String codigoProducto) {
        ProductoRetornoDTO productoEncontrado = productoService.findByCodigoProducto(codigoProducto);
        return ResponseEntity.ok(productoEncontrado);
    }

    // ✅ Filtrar productos por categoria
    @Operation(summary = "Filtrar los productos por categoria")
    @ApiResponse(responseCode = "200", description = "Se obtuvo el recurso correctamente")
    @ApiResponse(responseCode = "204", description = "Se obtuvo el recurso pero esta vacio")
    @ApiResponse(responseCode = "404", description = "No se encontro el recurso")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/filtrar")
    public ResponseEntity<List<ProductoRetornoDTO>> obtenerProductosPorCategoria(@RequestParam CategoriaEnum categoria) {
        List<ProductoRetornoDTO> productos = productoService.filtrarProductosPorCategoria(categoria);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

}
