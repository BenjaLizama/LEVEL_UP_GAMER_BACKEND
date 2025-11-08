package com.level_up.usuarios.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarUsuarioDTO {

    @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres.")
    private String nombre;

    @Size(min = 3, max = 30, message = "El apellido debe tener entre 3 y 30 caracteres.")
    private String apellido;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    @Pattern(regexp = "^[^\\s]+$", message = "La contraseña no puede contener espacios.")
    private String contrasena;

    @Pattern(regexp = "^[^\\s]+$", message = "El nombre de usuario no puede contener espacios.")
    @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres.")
    private String nombreUsuario;
}