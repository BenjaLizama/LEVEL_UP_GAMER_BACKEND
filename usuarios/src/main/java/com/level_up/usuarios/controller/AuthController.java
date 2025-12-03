package com.level_up.usuarios.controller;

import com.level_up.usuarios.dto.AgregarUsuarioDTO;
import com.level_up.usuarios.dto.LoginDTO;
import com.level_up.usuarios.dto.UsuarioRetornoDTO;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion de usuarios", description = "Gestiona la autenticacion de los usuarios")
public class AuthController {

    private final UsuarioService usuarioService;

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

    // ✅ Iniciar sesion
    @Operation(summary = "Iniciar sesion")
    @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso")
    @ApiResponse(responseCode = "401", description = "No se proporcionaron credenciales validas")
    @ApiResponse(responseCode = "500", description = "Error inesperado o no controlado")
    @PostMapping("/login")
    public ResponseEntity<UsuarioRetornoDTO> iniciarSesion(@RequestBody LoginDTO loginDTO, HttpSession session) {
        UsuarioRetornoDTO usuarioLogeado = usuarioService.iniciarSesion(loginDTO.getCorreo(), loginDTO.getContrasena());
        return ResponseEntity.ok(usuarioLogeado);
    }
}
