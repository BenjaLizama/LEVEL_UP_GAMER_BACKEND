package com.level_up.carrito.repository;

import com.level_up.carrito.model.CarritoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<CarritoModel, Long> {

    Optional<CarritoModel> findByIdUsuario(Long idUsuario);

}
