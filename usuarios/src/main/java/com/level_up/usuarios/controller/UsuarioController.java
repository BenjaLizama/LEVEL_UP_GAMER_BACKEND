package com.level_up.usuarios.controller;

import com.level_up.usuarios.dto.UsuarioDTO;
import com.level_up.usuarios.exception.UsuarioNotFoundException;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerUsuario(@PathVariable Long id) {
        UsuarioModel usuarioEncontrado = usuarioService.findById(id);
        return ResponseEntity.ok(usuarioEncontrado);
    }

    @PostMapping("/agregar")
    public ResponseEntity<UsuarioModel> agregarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioModel nuevoUsuario = usuarioService.save(usuarioDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping("/test404")
    public void test() {
        throw new UsuarioNotFoundException("Prueba error 404.");
    }
}
