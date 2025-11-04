package com.level_up.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioDTO {

    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "El formato del correo no es valido.")
    private String correo;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String contrasena;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(min = 3, max = 30, message = "El apellido debe tener entre 3 y 30 caracteres.")
    private String apellido;

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres.")
    private String nombreUsuario;

}
