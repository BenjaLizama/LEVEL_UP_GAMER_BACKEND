package com.level_up.usuarios.controller;

import com.level_up.usuarios.dto.ActualizarUsuarioDTO;
import com.level_up.usuarios.dto.AgregarUsuarioDTO;
import com.level_up.usuarios.dto.LoginDTO;
import com.level_up.usuarios.exception.UsuarioNotFoundException;
import com.level_up.usuarios.exception.UsuarioUpdateException;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Gestion de usuarios", description = "Endpoints para crear, leer, actualizar y eliminar usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Obtener usuario por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerUsuario(@PathVariable Long id) {
        UsuarioModel usuarioEncontrado = usuarioService.findById(id);
        return ResponseEntity.ok(usuarioEncontrado);
    }

    @Operation(summary = "Agregar usuario")
    @ApiResponse(responseCode = "200", description = "Usuario agregado")
    @PostMapping("/agregar")
    public ResponseEntity<UsuarioModel> agregarUsuario(@Valid @RequestBody AgregarUsuarioDTO agregarUsuarioDTO) {
        UsuarioModel nuevoUsuario = usuarioService.save(agregarUsuarioDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @Operation(summary = "Actualizar imagen de perfil del usuario")
    @ApiResponse(responseCode = "200", description = "Imagen de perfil actualizada")
    @PutMapping("/{idUsuario}/imagen")
    public ResponseEntity<UsuarioModel> actualizarImagenPerfil(
            @PathVariable Long idUsuario,
            @RequestParam(value = "imagen", required = false)MultipartFile imagen,
            @RequestParam(value = "urlImagen", required = false) String urlImagen
    ) {
        if (imagen == null && (urlImagen == null || urlImagen.isBlank())) {
            throw new UsuarioUpdateException("Debe enviar un archivo o una URL de imagen");
        }

        UsuarioModel usuarioActualizado = usuarioService.actualizarImagenPerfil(idUsuario, imagen, urlImagen);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @Operation(summary = "Iniciar sesion como usuario")
    @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso")
    @GetMapping("/login")
    public ResponseEntity<UsuarioModel> iniciarSesion(@RequestBody LoginDTO loginDTO) {
        UsuarioModel usuarioLogeado = usuarioService.validarCredenciales(loginDTO.getCorreo(), loginDTO.getContrasena());
        return ResponseEntity.ok(usuarioLogeado);
    }

    @Operation(summary = "Actualizar informacion del usuario")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado con exito")
    @PutMapping("/{idUsuario}/update")
    public ResponseEntity<UsuarioModel> actualizarInformacionUsuario(
            @PathVariable Long idUsuario,
            @Valid @RequestBody ActualizarUsuarioDTO actualizarUsuarioDTO
    ) {
        UsuarioModel usuarioActualizado = usuarioService.actualizarInformacionUsuario(idUsuario, actualizarUsuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @GetMapping("/test404")
    public void test() {
        throw new UsuarioNotFoundException("Prueba error 404.");
    }
}
