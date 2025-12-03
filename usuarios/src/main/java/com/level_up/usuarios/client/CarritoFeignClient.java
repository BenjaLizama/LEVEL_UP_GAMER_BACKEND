package com.level_up.usuarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ms-carrito", url = "http://localhost:8081/api/public/carritos")
public interface CarritoFeignClient {

    @PostMapping("/inicializar/{idUsuario}")
    void inicializarCarrito(@PathVariable("idUsuario") Long idUsuario);

}
