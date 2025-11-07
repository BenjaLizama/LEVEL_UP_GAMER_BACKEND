package com.level_up.productos.repository;

import com.level_up.productos.enums.CategoriaEnum;
import com.level_up.productos.model.ProductoModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {

    List<ProductoModel> findByCategoriaProducto(CategoriaEnum categoria);
    List<ProductoModel> findByPrecioProductoBetween(Double min, Double max);
    Long countByCategoriaProducto(CategoriaEnum categoria);
    Boolean existsByCodigoProducto(String codigoProducto);

    @Transactional
    @Modifying
    Long deleteByCodigoProducto(String codigoProducto);
}
