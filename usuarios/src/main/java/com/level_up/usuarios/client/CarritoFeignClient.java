package com.level_up.usuarios.client;

import com.level_up.usuarios.config.FeignClientConfig;
import com.level_up.usuarios.dto.TotalCarritoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(
        name = "ms-carrito",
        url = "http://localhost:8081",
        configuration = FeignClientConfig.class
)
public interface CarritoFeignClient {

    @PostMapping("/api/public/carritos/inicializar/{idUsuario}")
    void inicializarCarrito(@PathVariable("idUsuario") Long idUsuario);

    @GetMapping("/api/carritos/{idUsuario}")
    TotalCarritoDTO obtenerTotalCarrito(@PathVariable("idUsuario") Long idUsuario);

    @PutMapping("/api/carritos/{idUsuario}/vaciar")
    void vaciarCarrito(@PathVariable("idUsuario") Long idUsuario);
}