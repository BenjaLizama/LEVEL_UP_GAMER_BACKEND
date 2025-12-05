package com.level_up.carrito.controller;

import com.level_up.carrito.dto.AgregarItemDTO;
import com.level_up.carrito.dto.CarritoRetornoDTO;
import com.level_up.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/carritos")
@CrossOrigin
@Tag(name = "Gestion de carritos", description = "Endpoints para administrar el carrito del usuario")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // ✅ Agregar item
    @Operation(summary = "Agregar un ítem al carrito de un usuario o crear uno nuevo")
    @ApiResponse(responseCode = "200", description = "Item agregado o actualizado con exito")
    @ApiResponse(responseCode = "400", description = "Validacion fallida o stock insuficiente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @PostMapping("/{idUsuario}")
    public ResponseEntity<CarritoRetornoDTO> agregarItem(@PathVariable("idUsuario") Long idUsuario, @Valid @RequestBody AgregarItemDTO agregarItemDTO) {
        CarritoRetornoDTO carritoActualizado = carritoService.agregarItem(idUsuario, agregarItemDTO);
        return ResponseEntity.ok(carritoActualizado);
    }

    // ✅ Obtener carrito
    @Operation(summary = "Obtener el carrito de un usuario")
    @ApiResponse(responseCode = "200", description = "Carrito encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no tiene un carrito activo")
    @GetMapping("/{idUsuario}")
    public ResponseEntity<CarritoRetornoDTO> obtenerCarrito(@PathVariable("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(idUsuario));
    }

    // ✅ Eliminar item
    @Operation(summary = "Eliminar un producto especifico del carrito")
    @ApiResponse(responseCode = "200", description = "Item eliminado y carrito recalculado")
    @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado en el carrito")
    @DeleteMapping("/{idUsuario}/items/{codigoProducto}")
    public ResponseEntity<CarritoRetornoDTO> eliminarItem(@PathVariable("idUsuario") Long idUsuario, @PathVariable("codigoProducto") String codigoProducto) {
        CarritoRetornoDTO carritoActualizado = carritoService.eliminarItem(idUsuario, codigoProducto);
        return ResponseEntity.ok(carritoActualizado);
    }

    // ✅ Quitar item del carrito
    @Operation(summary = "Quita un item del carrito, si solo queda uno elimina el item completo")
    @ApiResponse(responseCode = "200", description = "1 Item eliminado y carrito recalculado")
    @ApiResponse(responseCode = "404", description = "Item no encontrado en el carrito")
    @PutMapping("/{idUsuario}/items/remover/{codigoProducto}")
    public ResponseEntity<CarritoRetornoDTO> quitarItemDelCarrito(@PathVariable("idUsuario") Long idUsuario, @PathVariable("codigoProducto") String codigoProducto) {
        CarritoRetornoDTO carritoActualizado = carritoService.quitarItemDelCarrito(idUsuario, codigoProducto);
        return ResponseEntity.ok(carritoActualizado);
    }

    // ✅ Vaciar carrito
    @Operation(summary = "Vacia el carrito del usuario")
    @ApiResponse(responseCode = "200", description = "El carrito se vacio correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontro el carrito del usuario")
    @PutMapping("/{idUsuario}/vaciar")
    public ResponseEntity<String> vaciarCarritoDelUsuario(@PathVariable("idUsuario") Long idUsuario) {
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.ok("Se ha vaciado el carrito del usuario " + idUsuario);
    }
}
