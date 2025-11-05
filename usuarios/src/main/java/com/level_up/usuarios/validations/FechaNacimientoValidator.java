package com.level_up.usuarios.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class FechaNacimientoValidator implements ConstraintValidator<FechaNacimientoValida, LocalDate> {
    @Override
    public boolean isValid(LocalDate fecha, ConstraintValidatorContext context) {
        if (fecha == null) return false;

        LocalDate hoy = LocalDate.now();
        // No puede ser fecha futura
        if (fecha.isAfter(hoy)) return false;
        // No puede tener mas de 150 años
        if (fecha.isBefore(hoy.minusYears(150))) return false;
        // No puede ser menor de 18 años
        if (Period.between(fecha, hoy).getYears() < 18) return false;

        return true;
    }
}
