package com.level_up.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoExternoDTO {

    private String codigoProducto;
    private Double precioProducto;
    private Integer cantidadStockProducto;
    private String nombreProducto;

}
