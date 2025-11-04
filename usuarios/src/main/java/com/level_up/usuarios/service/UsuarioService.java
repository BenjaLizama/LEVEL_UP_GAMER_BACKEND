package com.level_up.usuarios.service;

import com.level_up.usuarios.dto.UsuarioDTO;
import com.level_up.usuarios.exception.UsuarioNotFoundException;
import com.level_up.usuarios.exception.UsuarioSaveException;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional(rollbackOn = Exception.class)
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioModel save(UsuarioDTO usuarioDTO) {
        try {
            if (usuarioRepository.existsByCorreo(usuarioDTO.getCorreo())) {
                throw new UsuarioSaveException("El correo ya se encuentra registrado.");
            }

            UsuarioModel nuevoUsuario = new UsuarioModel();
            nuevoUsuario.setCorreo(usuarioDTO.getCorreo());

            String hash = passwordEncoder.encode(usuarioDTO.getContrasena());
            nuevoUsuario.setContrasena(hash);

            nuevoUsuario.setNombreUsuario(usuarioDTO.getNombreUsuario());
            nuevoUsuario.setNombre(usuarioDTO.getNombre());
            nuevoUsuario.setApellido(usuarioDTO.getApellido());

            usuarioRepository.save(nuevoUsuario);

            return nuevoUsuario;

        } catch (DataAccessException e) {
            throw new UsuarioSaveException("Error al guardar el usuario", e);
        }
    }

    public UsuarioModel findById(Long id) {
        try {
            return usuarioRepository.findById(id)
                    .orElseThrow(() ->
                            new UsuarioNotFoundException("El usuario con el ID: " + id + " no existe."));

        } catch (DataAccessException e) {
            throw new UsuarioNotFoundException("Error inesperado al buscar al usuario.", e);
        }
    }
}
