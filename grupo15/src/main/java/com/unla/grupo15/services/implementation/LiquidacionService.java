package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Cajero;
import com.unla.grupo15.entities.Cocinero;
import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.RolCocina;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class LiquidacionService {

    private static final double SUELDO_BASE = 100000.0;
    private static final double VALOR_ANIO_ANTIGUEDAD = 5000.0;

    private static final double PLUS_COCINERO = 150000.0;
    private static final double PLUS_AYUDANTE = 100000.0;
    private static final double PLUS_LAVAPLATOS = 50000.0;

    public double calcularSueldo(Personal personal, int diasDuracionFestival) {
        double subtotal = 0.0;

        if (personal instanceof Cajero) {
            int aniosAntiguedad = calcularAntiguedad(personal.getFechaIngreso());
            subtotal = SUELDO_BASE + (aniosAntiguedad * VALOR_ANIO_ANTIGUEDAD);
        } else if (personal instanceof Cocinero) {
            Cocinero cocinero = (Cocinero) personal;
            double plusCategoria = obtenerPlusPorCategoria(cocinero.getCategoria());
            subtotal = SUELDO_BASE + plusCategoria;
        } else {
            subtotal = SUELDO_BASE;
        }

        if (diasDuracionFestival <= 15) {
            return 0.5 * subtotal;
        } else if (diasDuracionFestival <= 30) {
            return 1.0 * subtotal;
        } else {
            int factor = (diasDuracionFestival / 15) + 1;
            return factor * 0.5 * subtotal;
        }
    }

    private int calcularAntiguedad(LocalDate fechaIngreso) {
        if (fechaIngreso == null) return 0;
        return Period.between(fechaIngreso, LocalDate.now()).getYears();
    }

    private double obtenerPlusPorCategoria(RolCocina categoria) {
        if (categoria == null) return 0.0;
        switch (categoria) {
            case COCINERO:
                return PLUS_COCINERO;
            case COCINERO_AYUDANTE:
                return PLUS_AYUDANTE;
            case LAVAPLATOS:
                return PLUS_LAVAPLATOS;
            default:
                return 0.0;
        }
    }
}