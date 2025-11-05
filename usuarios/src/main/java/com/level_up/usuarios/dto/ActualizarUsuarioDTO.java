package com.level_up.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarUsuarioDTO {

    @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres.")
    private String nombre;

    @Size(min = 3, max = 30, message = "El apellido debe tener entre 3 y 30 caracteres.")
    private String apellido;

    private String contrasena;

    @Pattern(regexp = "^[^\\s]+$", message = "El nombre de usuario no puede contener espacios.")
    @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres.")
    private String nombreUsuario;
}
