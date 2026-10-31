package com.unla.grupo15.dto;

import java.math.BigDecimal;

public class ReporteVentaDTO {

    private final String nombreFestival;
    private final BigDecimal recaudacionTotal;

    public ReporteVentaDTO(String nombreFestival, BigDecimal recaudacionTotal) {
        this.nombreFestival = nombreFestival;
        this.recaudacionTotal = recaudacionTotal;
    }

    public String getNombreFestival() {
        return nombreFestival;
    }

    public BigDecimal getRecaudacionTotal() {
        return recaudacionTotal;
    }
}
