package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Cajero;
import com.unla.grupo15.entities.Cocinero;
import com.unla.grupo15.entities.Costo;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.RolCocina;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Service
public class LiquidacionService {

    public BigDecimal calcularSueldo(Personal personal, Festival festival) {
        if (personal == null || festival == null
                || festival.getFechaInicio() == null || festival.getFechaFin() == null
                || festival.getCosto() == null || festival.getCosto().getSueldoBase() == null) {
            throw new IllegalStateException("Faltan datos del empleado o del festival para calcular la liquidación.");
        }

        long diasFestival = ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin()) + 1;
        if (diasFestival <= 0) {
            throw new IllegalStateException("La fecha de fin del festival debe ser igual o posterior a la fecha de inicio.");
        }

        BigDecimal subtotal = calcularSubtotal(personal, festival.getCosto(), festival.getFechaInicio());

        long periodos = (diasFestival + 14) / 15;

        BigDecimal factorPeriodos = BigDecimal.valueOf(periodos);
        BigDecimal porcentaje = new BigDecimal("0.5");

        return subtotal.multiply(factorPeriodos).multiply(porcentaje);
    }

    private BigDecimal calcularSubtotal(Personal personal, Costo costo, LocalDate fechaFestival) {
        BigDecimal subtotal = costo.getSueldoBase();
        if (personal instanceof Cocinero cocinero) {
            subtotal = subtotal.add(obtenerPlusPorCategoria(cocinero.getCategoria(), costo));
        } else if (personal instanceof Cajero) {
            subtotal = subtotal.add(BigDecimal.valueOf(
                    calcularAntiguedad(personal.getFechaIngreso(), fechaFestival) * 5000L));
        }
        return subtotal;
    }

    private int calcularAntiguedad(LocalDate fechaIngreso, LocalDate fechaFestival) {
        if (fechaIngreso == null || fechaFestival == null || fechaFestival.isBefore(fechaIngreso)) {
            return 0;
        }
        return Period.between(fechaIngreso, fechaFestival).getYears();
    }

    private BigDecimal obtenerPlusPorCategoria(RolCocina categoria, Costo costo) {
        if (categoria == null) return BigDecimal.ZERO;
        switch (categoria) {
            case COCINERO:
                return costo.getPlusCocinero() == null ? BigDecimal.ZERO : costo.getPlusCocinero();
            case COCINERO_AYUDANTE:
                return costo.getPlusCocineroAyudante() == null ? BigDecimal.ZERO : costo.getPlusCocineroAyudante();
            case LAVAPLATOS:
                return costo.getPlusLavaplatos() == null ? BigDecimal.ZERO : costo.getPlusLavaplatos();
            default:
                return BigDecimal.ZERO;
        }
    }
}