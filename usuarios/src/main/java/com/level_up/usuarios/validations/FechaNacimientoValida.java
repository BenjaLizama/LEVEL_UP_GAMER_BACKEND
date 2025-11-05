package com.level_up.usuarios.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FechaNacimientoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FechaNacimientoValida {
    String message() default "Fecha de nacimiento invalida.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
