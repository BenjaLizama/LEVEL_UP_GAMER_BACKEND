package com.level_up.productos.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductoRetornoDTO {
    private String codigoProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private Double precioProducto;
    private String categoria;
    private List<String> imagenesUrl;
    private Integer cantidadStockProducto;

    public String getCategoria() {
        return categoria.replace("_", " ");
    }
}
