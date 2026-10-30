package com.unla.grupo15.dto;

import java.math.BigDecimal;

public class ReporteMayoresCanonDTO {

    private final String nombreComercial;
    private final String codigoUnico;
    private final String tipoUnidad;
    private final BigDecimal canon;

    public ReporteMayoresCanonDTO(
            String nombreComercial,
            String codigoUnico,
            String tipoUnidad,
            BigDecimal canon) {
        this.nombreComercial = nombreComercial;
        this.codigoUnico = codigoUnico;
        this.tipoUnidad = tipoUnidad;
        this.canon = canon;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public String getTipoUnidad() {
        return tipoUnidad;
    }

    public BigDecimal getCanon() {
        return canon;
    }
}
