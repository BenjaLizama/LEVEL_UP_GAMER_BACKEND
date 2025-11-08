package com.level_up.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoRetornoDTO {

    private Long idCarrito;
    private Long idUsuario;
    private Double total;
    private List<ItemCarritoRetornoDTO> items;

}
