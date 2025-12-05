package com.level_up.productos.service;

import com.level_up.productos.dto.ProductoDTO;
import com.level_up.productos.dto.ProductoRetornoDTO;
import com.level_up.productos.enums.CategoriaEnum;
import com.level_up.productos.model.ProductoModel;
import com.level_up.productos.model.StockModel;
import com.level_up.productos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class ProductoServiceTest {
    @Mock
    private ProductoRepository productoRepository;
    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {

        stockModelFalso = new StockModel();
        stockModelFalso.setCantidad(1);
        productoDtoFalso = new ProductoDTO();

        productoFalso = new ProductoModel();

        productoFalso.setNombreProducto("mouse 1");
        productoFalso.setCodigoProducto("M1");
        productoFalso.setPrecioProducto(1000.0);
        productoFalso.setCategoriaProducto(CategoriaEnum.MOUSE);
        productoFalso.setStock(stockModelFalso);


        productoDtoFalso.setNombreProducto("mouse 1");
        productoDtoFalso.setImagenesUrl(List.of("imagen_1"));
        productoDtoFalso.setPrecioProducto(1000.0);
        productoDtoFalso.setDescripcionProducto("un mouse");
        productoDtoFalso.setCategoriaProducto(CategoriaEnum.MOUSE);
        productoDtoFalso.setCantidadInicial(10);


    }

    private ProductoModel productoFalso;

    private StockModel stockModelFalso;

    private ProductoDTO productoDtoFalso;

    @Test
    void agregarProducto() {

        when(productoRepository.save(any(ProductoModel.class))).thenReturn(productoFalso);

        ProductoRetornoDTO resultado = productoService.agregarProducto(productoDtoFalso);

        assertEquals("mouse 1", resultado.getNombreProducto());


    }

    @Test
    void eliminarProducto() {
        String codigoABorrar = "M1";
        when(productoRepository.deleteByCodigoProducto(codigoABorrar)).thenReturn(1L);

        productoService.eliminarProducto(codigoABorrar);

        verify(productoRepository, times(1)).deleteByCodigoProducto(codigoABorrar);


    }

    @Test
    void findAll() {


        List<ProductoModel> listaFalsa = List.of(productoFalso);

        when(productoRepository.findAll()).thenReturn(listaFalsa);

        List<ProductoRetornoDTO> resultado = productoService.findAll();
        assertEquals("mouse 1", resultado.get(0).getNombreProducto());


    }

    @Test
    void findById() {


        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoFalso));

        ProductoRetornoDTO resultado = productoService.findById(1L);

        assertEquals("mouse 1", resultado.getNombreProducto());
    }

    @Test
    void findByCodigoProducto() {


        when(productoRepository.findByCodigoProducto("M1")).thenReturn(Optional.of(productoFalso));

        ProductoRetornoDTO resultado = productoService.findByCodigoProducto("M1");

        assertEquals("mouse 1", resultado.getNombreProducto());
    }

    @Test
    void existsById() {


        when(productoRepository.existsById(1L)).thenReturn(true);

        Boolean resultado = productoService.existsById(1L);

        assertEquals(true, resultado);
    }

    @Test
    void filtrarProductosPorCategoria() {
        List<ProductoModel> listaFalsa = List.of(productoFalso);

        when(productoRepository.findByCategoriaProducto(productoFalso.getCategoriaProducto())).thenReturn(listaFalsa);

        List<ProductoRetornoDTO> resultado = productoService.filtrarProductosPorCategoria(productoFalso.getCategoriaProducto());

        assertEquals(productoFalso.getCodigoProducto(), resultado.get(0).getCodigoProducto());
        verify(productoRepository, times(1)).findByCategoriaProducto(productoFalso.getCategoriaProducto());
    }

    @Test
    void actualizarProducto() {

        String codigo = "MOUSE-1";


        ProductoModel productoEnBD = new ProductoModel();
        productoEnBD.setCodigoProducto(codigo);
        productoEnBD.setNombreProducto("Nombre Viejo");
        productoEnBD.setPrecioProducto(500.0);
        productoEnBD.setCategoriaProducto(CategoriaEnum.MOUSE);

        StockModel stockExistente = new StockModel();
        stockExistente.setCantidad(10);
        productoEnBD.setStock(stockExistente);


        ProductoDTO datosNuevos = new ProductoDTO();
        datosNuevos.setNombreProducto("Nombre Nuevo y Mejorado");
        datosNuevos.setPrecioProducto(999.0);
        datosNuevos.setCantidadInicial(50);

        when(productoRepository.findByCodigoProducto(codigo))
                .thenReturn(Optional.of(productoEnBD));


        when(productoRepository.save(any(ProductoModel.class)))
                .thenReturn(productoEnBD);


        ProductoRetornoDTO resultado = productoService.actualizarProducto(codigo, datosNuevos);


        assertEquals("Nombre Nuevo y Mejorado", resultado.getNombreProducto());
        assertEquals(999.0, resultado.getPrecioProducto());


        assertEquals(50, productoEnBD.getStock().getCantidad());


        verify(productoRepository, times(1)).save(productoEnBD);

    }
}