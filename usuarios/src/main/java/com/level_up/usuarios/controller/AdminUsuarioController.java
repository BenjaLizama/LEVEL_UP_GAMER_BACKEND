package com.level_up.usuarios.controller;

import com.level_up.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/usuarios")
@Tag(name = "Administracion de usuarios", description = "Endpoints para la gestion de usuarios de los administradores.")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    // ✅ Eliminar usuario
    @Operation(summary = "Eliminar usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado con exito")
    @ApiResponse(responseCode = "404", description = "No se ha encontrado el usuario")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @DeleteMapping("/eliminar/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("idUsuario") Long idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
