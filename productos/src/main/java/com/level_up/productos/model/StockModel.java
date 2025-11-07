package com.level_up.productos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "stock")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStock;

    private Integer cantidad;

    @OneToOne
    @JoinColumn(name = "id_producto", nullable = false)
    @ToString.Exclude
    @JsonBackReference
    private ProductoModel producto;
}
