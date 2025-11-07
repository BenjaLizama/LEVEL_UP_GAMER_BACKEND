package com.level_up.productos.dto;

import com.level_up.productos.enums.CategoriaEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
public class ProductoDTO {

    @NotBlank(message = "El nombre del producto no puede estar vacio.")
    @Size(max = 60, message = "El nombre no puede contener mas de 60 caracteres.")
    private String nombreProducto;

    @NotBlank(message = "La descripcion del producto no puede estar vacia.")
    @Size(max = 220, message = "La descripcion no debe contener mas de 220 caracteres.")
    private String descripcionProducto;

    @NotNull(message = "El precio del producto no puede ser nulo.")
    @Min(value = 1, message = "El precio del producto no puede ser cero o menor.")
    private Double precioProducto;

    @NotNull(message = "La categoria no puede ser nula.")
    private CategoriaEnum categoriaProducto;

    @NotNull(message = "La lista de imagenes no puede ser nula.")
    @Size(min = 1, message = "Debes añadir al menos una imagen.")
    private List<@URL(message = "Una de las imagenes no es una URL valida") String> imagenesUrl;

    // Datos para el stock
    @NotNull(message = "La cantidad inicial es obligatoria.")
    @Min(value = 0, message = "La cantidad no puede ser negativa.")
    private Integer cantidadInicial;
}
