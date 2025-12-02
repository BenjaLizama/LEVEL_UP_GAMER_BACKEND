package com.level_up.usuarios.controller;

import com.level_up.usuarios.dto.ActualizarUsuarioDTO;
import com.level_up.usuarios.dto.AgregarUsuarioDTO;
import com.level_up.usuarios.dto.LoginDTO;
import com.level_up.usuarios.dto.UsuarioRetornoDTO;
import com.level_up.usuarios.exception.UsuarioUpdateException;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
@Tag(name = "Gestion de usuarios", description = "Endpoints para crear, leer, actualizar y eliminar usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ✅ Obtener usuario
    @Operation(summary = "Obtener usuario por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "No se encontro el recurso")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerUsuario(@PathVariable("id") Long id) {
        UsuarioModel usuarioEncontrado = usuarioService.findById(id);
        return ResponseEntity.ok(usuarioEncontrado);
    }

    // ✅ Crear usuario
    @Operation(summary = "Agregar usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado con exito")
    @ApiResponse(responseCode = "409", description = "Conflicto en los campos, violacion de restriccion")
    @ApiResponse(responseCode = "422", description = "Los datos son validos pero no tienen sentido")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @PostMapping
    public ResponseEntity<UsuarioModel> agregarUsuario(@Valid @RequestBody AgregarUsuarioDTO agregarUsuarioDTO) {
        UsuarioModel nuevoUsuario = usuarioService.save(agregarUsuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // ✅ Actualizar imagen de perfil
    @Operation(summary = "Actualizar imagen de perfil del usuario")
    @ApiResponse(responseCode = "200", description = "Imagen de perfil actualizada")
    @ApiResponse(responseCode = "400", description = "Se enviaron datos mal formados o invalidos")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @PutMapping("/{idUsuario}/imagen")
    public ResponseEntity<UsuarioRetornoDTO> actualizarImagenPerfil(
         @PathVariable("idUsuario") Long idUsuario,
         @RequestParam(value = "imagen", required = false) MultipartFile imagen,
         @RequestParam(value = "urlImagen", required = false) String urlImagen
    ) {
        if (imagen == null && (urlImagen == null || urlImagen.isBlank())) {
            throw new UsuarioUpdateException("Debe enviar un archivo o una URL de imagen");
        }

        UsuarioModel usuarioActualizado = usuarioService.actualizarImagenPerfil(idUsuario, imagen, urlImagen);
        String urlPublica = usuarioService.getImagenPerfil(usuarioActualizado);
        UsuarioRetornoDTO dto = new UsuarioRetornoDTO();

        dto.setIdUsuario(usuarioActualizado.getIdUsuario());
        dto.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        dto.setNombre(usuarioActualizado.getNombre());
        dto.setApellido(usuarioActualizado.getApellido());
        dto.setCorreo(usuarioActualizado.getCorreo());
        dto.setImagenPerfilURL(urlPublica);

        return ResponseEntity.ok(dto);
    }

    // ✅ Iniciar sesion
    @Operation(summary = "Iniciar sesion como usuario")
    @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso")
    @ApiResponse(responseCode = "401", description = "No se proporcionaron credenciales validas")
    @ApiResponse(responseCode = "500", description = "Error inesperado o no controlado")
    @PostMapping("/login")
    public ResponseEntity<UsuarioRetornoDTO> iniciarSesion(@RequestBody LoginDTO loginDTO, HttpSession session) {
        UsuarioRetornoDTO usuarioLogeado = usuarioService.iniciarSesion(loginDTO.getCorreo(), loginDTO.getContrasena());
        return ResponseEntity.ok(usuarioLogeado);
    }

    // ✅ Cerrar sesion
    @Operation(summary = "Cerrar la sesion")
    @ApiResponse(responseCode = "204", description = "El usuario cerro sesion con exito")
    @ApiResponse(responseCode = "500", description = "Error inesperado o no controlado")
    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    // ✅ Actualizar usuario
    @Operation(summary = "Actualizar informacion del usuario")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado con exito")
    @ApiResponse(responseCode = "404", description = "No se ha encontrado el usuario")
    @ApiResponse(responseCode = "409", description = "Conflicto en los campos, violacion de restriccion")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @PutMapping("/{idUsuario}")
    public ResponseEntity<UsuarioModel> actualizarInformacionUsuario(
            @PathVariable("idUsuario") Long idUsuario,
            @Valid @RequestBody ActualizarUsuarioDTO actualizarUsuarioDTO
    ) {
        UsuarioModel usuarioActualizado = usuarioService.actualizarInformacionUsuario(idUsuario, actualizarUsuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // ✅ Eliminar usuario
    @Operation(summary = "Eliminar usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado con exito")
    @ApiResponse(responseCode = "404", description = "No se ha encontrado el usuario")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("idUsuario") Long idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
