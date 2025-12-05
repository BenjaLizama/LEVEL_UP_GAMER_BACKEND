package com.level_up.carrito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.level_up.carrito.dto.AgregarItemDTO;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test

    void agregarItem() {
        AgregarItemDTO itemDTO = new AgregarItemDTO();
        itemDTO.setCantidad(10);
        itemDTO.setCodigoProducto("PROD_1");

    }

    @Test
    void obtenerCarrito() {
    }

    @Test
    void eliminarItem() {
    }

    @Test
    void vaciarCarritoDelUsuario() {
    }
}