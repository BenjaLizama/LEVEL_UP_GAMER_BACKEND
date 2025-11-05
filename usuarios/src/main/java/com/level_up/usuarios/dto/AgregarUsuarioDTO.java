package com.level_up.usuarios.dto;

import com.level_up.usuarios.validations.FechaNacimientoValida;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AgregarUsuarioDTO {

    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "El formato del correo no es valido.")
    private String correo;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Pattern(regexp = "^[^\\s]+$", message = "La contraseña no puede contener espacios.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String contrasena;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(min = 3, max = 30, message = "El apellido debe tener entre 3 y 30 caracteres.")
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @FechaNacimientoValida
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Pattern(regexp = "^[^\\s]+$", message = "El nombre de usuario no puede contener espacios.")
    @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres.")
    private String nombreUsuario;
}
