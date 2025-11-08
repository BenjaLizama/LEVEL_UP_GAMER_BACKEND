package com.level_up.carrito.client;

import com.level_up.carrito.dto.ProductoExternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-productos", url = "http://localhost:8082/api/productos")
public interface ProductoFeignClient {

    @GetMapping("/code/{codigoProducto}")
    ProductoExternoDTO obtenerProductoPorCodigo(@PathVariable("codigoProducto") String codigoProducto);

}
