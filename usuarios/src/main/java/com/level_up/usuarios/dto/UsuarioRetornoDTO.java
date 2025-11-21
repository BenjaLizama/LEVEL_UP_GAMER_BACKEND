package com.level_up.usuarios.dto;

import lombok.Data;

@Data
public class UsuarioRetornoDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String token;
}
