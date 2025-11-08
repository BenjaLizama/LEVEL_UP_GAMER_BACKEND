package com.level_up.carrito.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "carritos")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrito;

    @Column(nullable = false, unique = true)
    private Long idUsuario;

    private Double total = 0.0;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarritoModel> items = new ArrayList<>();

}
