package com.level_up.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarritoRetornoDTO {
    private String codigoProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
