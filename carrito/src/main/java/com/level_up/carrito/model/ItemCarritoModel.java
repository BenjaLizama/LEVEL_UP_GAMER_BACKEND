package com.level_up.carrito.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "items_carrito")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarritoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;

    @Column(nullable = false)
    private String codigoProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCarrito", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private CarritoModel carrito;

    public Double getSubTotal() {
        return this.precioUnitario * this.cantidad;
    }

}
