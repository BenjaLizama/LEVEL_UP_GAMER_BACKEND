package com.level_up.usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "metodo_pago")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetodoPagoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMetodoPago;

    private String tipo;
    private String numero;
    private String titular;
    private String fechaExpiracion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioModel usuario;
}
