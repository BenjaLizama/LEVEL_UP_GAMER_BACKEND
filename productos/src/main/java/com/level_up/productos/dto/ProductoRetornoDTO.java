package com.level_up.productos.dto;

import lombok.Data;

@Data
public class ProductoRetornoDTO {
    private String codigoProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private Double precioProducto;
    private Integer stockProducto;
}
