package com.level_up.productos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.level_up.productos.dto.ProductoDTO;
import com.level_up.productos.enums.CategoriaEnum;
import com.level_up.productos.model.ProductoModel;
import com.level_up.productos.model.StockModel;
import com.level_up.productos.repository.ProductoRepository;
import com.level_up.productos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase(replace = org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY)
@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();

    }

    //AGREGAR PRODUCTO

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void agregarProducto() throws Exception {
        ProductoDTO productoDtoPrueba = new ProductoDTO();
        productoDtoPrueba.setNombreProducto("mouse 1");
        productoDtoPrueba.setCategoriaProducto(CategoriaEnum.MOUSE);
        productoDtoPrueba.setPrecioProducto(10000.0);
        productoDtoPrueba.setImagenesUrl(List.of("https://img.lb.wbmdstatic.com/vim/live/webmd/consumer_assets/site_images/article_thumbnails/BigBead/what_to_know_about_house_mice_bigbead/1800x1200_getty_rf_what_to_know_about_house_mice_bigbead.jpg"));
        productoDtoPrueba.setDescripcionProducto("soy un mouse");
        productoDtoPrueba.setCantidadInicial(10);

        String jsonRequest = objectMapper.writeValueAsString(productoDtoPrueba);

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)) // Tu JSON de producto
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(roles = "USER")
    void agregarProducto_User() throws Exception {
        ProductoDTO productoDtoPrueba = new ProductoDTO();
        productoDtoPrueba.setNombreProducto("mouse 1");
        productoDtoPrueba.setCategoriaProducto(CategoriaEnum.MOUSE);
        productoDtoPrueba.setPrecioProducto(10000.0);
        productoDtoPrueba.setImagenesUrl(List.of("https://img.lb.wbmdstatic.com/vim/live/webmd/consumer_assets/site_images/article_thumbnails/BigBead/what_to_know_about_house_mice_bigbead/1800x1200_getty_rf_what_to_know_about_house_mice_bigbead.jpg"));
        productoDtoPrueba.setDescripcionProducto("soy un mouse");
        productoDtoPrueba.setCantidadInicial(10);

        String jsonRequest = objectMapper.writeValueAsString(productoDtoPrueba);

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().isForbidden());

    }


    @Test
    void agregarProducto_SinUser() throws Exception {

        ProductoDTO productoDtoPrueba = new ProductoDTO();
        productoDtoPrueba.setNombreProducto("mouse 1");
        productoDtoPrueba.setCategoriaProducto(CategoriaEnum.MOUSE);
        productoDtoPrueba.setPrecioProducto(10000.0);
        productoDtoPrueba.setImagenesUrl(List.of("https://img.lb.wbmdstatic.com/vim/live/webmd/consumer_assets/site_images/article_thumbnails/BigBead/what_to_know_about_house_mice_bigbead/1800x1200_getty_rf_what_to_know_about_house_mice_bigbead.jpg"));
        productoDtoPrueba.setDescripcionProducto("soy un mouse");
        productoDtoPrueba.setCantidadInicial(10);

        String jsonRequest = objectMapper.writeValueAsString(productoDtoPrueba);

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().isForbidden());

    }

    //ELIMINAR PRODUCTOS
     @Test
     @WithMockUser(username = "ADMIN",roles = "ADMIN")
     void eliminarProducto_Admin() throws Exception{
         ProductoModel producto = new ProductoModel();

         producto.setNombreProducto("mouse 1");
         producto.setCategoriaProducto(CategoriaEnum.MOUSE);
         producto.setCodigoProducto("test_1");
         producto.setPrecioProducto(10000.0);
         producto.setImagenesUrl(List.of("imagen ej"));
         producto.setDescripcionProducto("soy un mouse");
         producto.setCodigoProducto("M1");

         StockModel stock = new StockModel();
         stock.setCantidad(10);
         stock.setProducto(producto);
         producto.setStock(stock);

         productoRepository.save(producto);



         mockMvc.perform(delete("/api/productos/delete/{id}", "M1")).andExpect(status().isNoContent());




     }



    @Test
    @WithMockUser(username = "USER", roles = "USER")
    void eliminarProducto_ConUser() throws Exception {

        String id = "JM_1";
        mockMvc.perform(delete("/api/productos/delete/{id}", id)).andExpect(status().isForbidden());
    }

    @Test
    void eliminarProducto_SinUser() throws Exception {

        String id = "JM_1";
        mockMvc.perform(delete("/api/productos/delete/{id}", id)).andExpect(status().isForbidden());
    }

    //OBTENER LISTA DE PRODUCTOS (ES UN METODO PUBLICO)

    @Test
    void obtenerTodosLosProductos() throws Exception {

        ProductoModel producto = new ProductoModel();

        producto.setNombreProducto("mouse 1");
        producto.setCategoriaProducto(CategoriaEnum.MOUSE);
        producto.setCodigoProducto("test_1");
        producto.setPrecioProducto(10000.0);
        producto.setImagenesUrl(List.of("imagen ej"));
        producto.setDescripcionProducto("soy un mouse");

        StockModel stock = new StockModel();
        stock.setCantidad(10);
        stock.setProducto(producto);
        producto.setStock(stock);
        productoRepository.save(producto);

        mockMvc.perform(get("/api/productos").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].nombreProducto", is("mouse 1")));
        ;

    }

}