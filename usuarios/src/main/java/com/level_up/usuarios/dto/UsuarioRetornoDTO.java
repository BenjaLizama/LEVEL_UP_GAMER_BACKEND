package com.level_up.usuarios.dto;

import lombok.Data;

@Data
public class UsuarioRetornoDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String imagenPerfilURL;
    private String token;
    private String rol;
}
