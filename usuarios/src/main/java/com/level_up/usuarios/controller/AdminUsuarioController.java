package com.level_up.usuarios.controller;

import com.level_up.usuarios.dto.AgregarUsuarioDTO;
import com.level_up.usuarios.dto.UsuarioRetornoDTO;
import com.level_up.usuarios.model.UsuarioModel;
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
    @ApiResponse(responseCode = "403", description = "No hay autorizacion para realizar operacion")
    @ApiResponse(responseCode = "404", description = "No se ha encontrado el usuario")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @DeleteMapping("/eliminar/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("idUsuario") Long idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

    // ✅ Agregar usuario administrador
    @Operation(summary = "Agregar usuario administrador")
    @ApiResponse(responseCode = "201", description = "Usuario creado con exito")
    @ApiResponse(responseCode = "400", description = "Error al crear el usuario")
    @ApiResponse(responseCode = "403", description = "No hay autorizacion para realizar operacion")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @PostMapping("/agregar")
    public ResponseEntity<UsuarioModel> agregarUsuarioAdministrador(@RequestBody AgregarUsuarioDTO usuario) {
        UsuarioModel nuevoAdmin = usuarioService.saveAdmin(usuario);
        return ResponseEntity.ok(nuevoAdmin);
    }
}
