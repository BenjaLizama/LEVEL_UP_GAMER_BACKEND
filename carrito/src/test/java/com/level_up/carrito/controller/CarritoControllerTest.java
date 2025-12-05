package com.level_up.carrito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.level_up.carrito.client.ProductoFeignClient;
import com.level_up.carrito.dto.AgregarItemDTO;
import com.level_up.carrito.dto.ProductoExternoDTO;
import com.level_up.carrito.repository.CarritoRepository;
import com.level_up.carrito.service.CarritoService;
import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase(replace = org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class CarritoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CarritoService carritoService;
    @Autowired
    private CarritoRepository carritoRepository;

    @MockBean
    private ProductoFeignClient productoFeignClient;
    @BeforeEach
    void setUp(){
        carritoRepository.deleteAll();

    }

    @Test
    void agregarItem() throws Exception{

        carritoService.inicializarCarrito(1L);

        AgregarItemDTO agregarItemDTO = new AgregarItemDTO();
        agregarItemDTO.setCodigoProducto("1122");
        agregarItemDTO.setCantidad(1);

        String requestContent = objectMapper.writeValueAsString(agregarItemDTO);

        mockMvc.perform(post("/api/carritos/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(requestContent)).andExpect(status().isForbidden());

    };



    @Test
    void obtenerCarrito() throws Exception {
        mockMvc.perform(get("/api/carritos/{id}", 1)).andExpect(status().isForbidden());

    }
    @Test
    @WithMockUser(roles = "ADMIN",username = "ADMIN")
    void obtenerCarritoAdmin() throws Exception {
        carritoService.inicializarCarrito(1L);
        mockMvc.perform(get("/api/carritos/{id}", 1)).andExpect(status().isOk());

    }



}