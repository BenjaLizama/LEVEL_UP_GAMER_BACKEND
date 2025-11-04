package com.level_up.usuarios.repository;

import com.level_up.usuarios.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    Boolean existsByCorreo(String correo);
    Optional<UsuarioModel> findById(Long id);

}
