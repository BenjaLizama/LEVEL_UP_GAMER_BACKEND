package com.level_up.productos.service;

import com.level_up.productos.dto.ProductoDTO;
import com.level_up.productos.dto.ProductoRetornoDTO;
import com.level_up.productos.enums.CategoriaEnum;
import com.level_up.productos.exception.ListaProductosException;
import com.level_up.productos.exception.ProductoDeleteException;
import com.level_up.productos.exception.ProductoNotFoundException;
import com.level_up.productos.exception.ProductoSaveException;
import com.level_up.productos.model.ProductoModel;
import com.level_up.productos.model.StockModel;
import com.level_up.productos.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoRetornoDTO agregarProducto(ProductoDTO productoDTO) {
        try {
            CategoriaEnum categoriaActual = productoDTO.getCategoriaProducto();
            String prefijo = categoriaActual.getPrefijo();
            String indice = obtenerIndiceProductoRegistradoPorCategoria(categoriaActual);

            ProductoModel nuevoProducto = new ProductoModel();
            StockModel nuevoStock = new StockModel();

            nuevoProducto.setNombreProducto(productoDTO.getNombreProducto());
            nuevoProducto.setDescripcionProducto(productoDTO.getDescripcionProducto());
            nuevoProducto.setPrecioProducto(productoDTO.getPrecioProducto());
            nuevoProducto.setCategoriaProducto(categoriaActual);

            if (productoDTO.getImagenesUrl().isEmpty()) {
                throw new ProductoSaveException("Error al agregar el producto, debe contener al menos 1 imagen");
            }
            nuevoProducto.setImagenesUrl(productoDTO.getImagenesUrl());

            String codigoProducto = prefijo + "-" + indice;

            if (productoRepository.existsByCodigoProducto(codigoProducto)) {
                throw new ProductoSaveException("No se puede insertar un producto con el codigo: " + codigoProducto + " porque ya existe");
            }

            nuevoProducto.setCodigoProducto(codigoProducto);

            nuevoStock.setCantidad(productoDTO.getCantidadInicial());

            nuevoProducto.setStock(nuevoStock);
            nuevoStock.setProducto(nuevoProducto);

            ProductoModel productoGuardado = productoRepository.save(nuevoProducto);

            ProductoRetornoDTO retorno = mapperProductoRetorno(productoGuardado);

            return retorno;

        } catch (DataAccessException e) {
            throw new ProductoSaveException("Error inesperado al guardar el producto: " + e.getMessage(), e);
        }
    }

    public void eliminarProducto(String codigoProducto) {
        try {
            Long filasEliminadas = productoRepository.deleteByCodigoProducto(codigoProducto);

            if (filasEliminadas == 0) {
                throw new ProductoNotFoundException("El producto con codigo " + codigoProducto + " no existe");
            }
        } catch (DataAccessException e) {
            throw new ProductoDeleteException("Error inesperado al eliminar el producto: " + e.getMessage(), e);
        }
    }

    public List<ProductoRetornoDTO> findAll() {
        List<ProductoModel> lista_productos = productoRepository.findAll();

        if (lista_productos.isEmpty()) {
            throw new ListaProductosException("No hay productos que mostrar");
        }

        return lista_productos.stream()
                .map(producto -> mapperProductoRetorno(producto))
                .toList();
    }

    public ProductoRetornoDTO findById(Long productoId) {
        try {
            ProductoModel producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new ProductoNotFoundException("No se encontro el producto con ID: " + productoId));

            ProductoRetornoDTO productoMapeado = mapperProductoRetorno(producto);

            return productoMapeado;
        } catch (DataAccessException e) {
            throw new ProductoNotFoundException("Error inesperado al buscar el producto: " + e.getMessage());
        }
    }

    public ProductoRetornoDTO findByCodigoProducto(String codigoProducto) {
        try {

            ProductoModel productoEncontrado = productoRepository.findByCodigoProducto(codigoProducto)
                    .orElseThrow(() -> new ProductoNotFoundException("El producto con codigo: " + codigoProducto + " no existe"));

            return mapperProductoRetorno(productoEncontrado);

        } catch (DataAccessException e) {
            throw new ProductoNotFoundException("Error inesperado al buscar el producto: " + e.getMessage(), e);
        }
    }

    public boolean existsById(Long productoId) {
        try {
            return productoRepository.existsById(productoId);

        } catch (DataAccessException e) {
            throw new ProductoNotFoundException("Error inesperado al buscar el producto" + e.getMessage());
        }
    }

    public List<ProductoRetornoDTO> filtrarProductosPorCategoria(CategoriaEnum categoria) {
        List<ProductoModel> lista_productos_encontrados = productoRepository.findByCategoriaProducto(categoria);
        return lista_productos_encontrados.stream()
                .map(producto -> mapperProductoRetorno(producto))
                .toList();
    }

    /*
    public List<ProductoModel> filtrarProductosPorPrecio(Double min, Double max) {
        try {
            List<ProductoModel> lista_productos_encontrados = productoRepository.findByPrecioProductoBetween(min, max);

            if (lista_productos_encontrados.isEmpty()) {
                throw new ListaProductosException("No existen productos en el rango de precios");
            }

            return lista_productos_encontrados;
        } catch (DataAccessException e) {
            throw new ListaProductosException("Error inesperado al filtrar productos por precio: " + e.getMessage(), e);
        }
    } */

    public ProductoRetornoDTO actualizarProducto(String codigoProducto, ProductoDTO productoDTO) {
        ProductoModel producto = productoRepository.findByCodigoProducto(codigoProducto)
                .orElseThrow(() -> new ProductoNotFoundException("El producto con codigo: " + codigoProducto + " no existe"));

        if (productoDTO.getNombreProducto() != null && !productoDTO.getNombreProducto().isBlank()) {
            producto.setNombreProducto(productoDTO.getNombreProducto());
        }

        if (productoDTO.getDescripcionProducto() != null && !productoDTO.getDescripcionProducto().isBlank()) {
            producto.setDescripcionProducto(productoDTO.getDescripcionProducto());
        }

        if (productoDTO.getPrecioProducto() != null && productoDTO.getPrecioProducto() > 0) {
            producto.setPrecioProducto(productoDTO.getPrecioProducto());
        }

        if (productoDTO.getImagenesUrl() != null && !productoDTO.getImagenesUrl().isEmpty()) {
            producto.setImagenesUrl(productoDTO.getImagenesUrl());
        }

        if (productoDTO.getCantidadInicial() != null && productoDTO.getCantidadInicial() >= 0) {
            producto.getStock().setCantidad(productoDTO.getCantidadInicial());
        }

        productoRepository.save(producto);

        return mapperProductoRetorno(producto);
    }

    private String obtenerIndiceProductoRegistradoPorCategoria(CategoriaEnum categoria) {
        Long cantidadActual = productoRepository.countByCategoriaProducto(categoria);
        Long nuevaCantidad = cantidadActual + 1;
        return String.format("%03d", nuevaCantidad);
    }

    private ProductoRetornoDTO mapperProductoRetorno(ProductoModel producto) {
        ProductoRetornoDTO productoRetorno = new ProductoRetornoDTO();

        productoRetorno.setCodigoProducto(producto.getCodigoProducto());
        productoRetorno.setNombreProducto(producto.getNombreProducto());
        productoRetorno.setDescripcionProducto(producto.getDescripcionProducto());
        productoRetorno.setPrecioProducto(producto.getPrecioProducto());
        productoRetorno.setImagenesUrl(producto.getImagenesUrl());
        productoRetorno.setCantidadStockProducto(producto.getStock().getCantidad());

        return productoRetorno;
    }
}
