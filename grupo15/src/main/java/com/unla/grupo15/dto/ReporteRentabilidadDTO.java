package com.unla.grupo15.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteRentabilidadDTO {

    private final BigDecimal ventasTotales;
    private final BigDecimal costosPlatos;
    private final BigDecimal sueldos;
    private final BigDecimal canon;
    private final BigDecimal rentabilidadNeta;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    public ReporteRentabilidadDTO(BigDecimal ventasTotales, BigDecimal costosPlatos,
                                  BigDecimal sueldos, BigDecimal canon) {
        this.ventasTotales = ventasTotales;
        this.costosPlatos = costosPlatos;
        this.sueldos = sueldos;
        this.canon = canon;
        this.rentabilidadNeta = ventasTotales
                .subtract(costosPlatos)
                .subtract(sueldos)
                .subtract(canon);
        this.fechaDesde = null;
        this.fechaHasta = null;
    }

    public ReporteRentabilidadDTO(BigDecimal ventasTotales, BigDecimal costosPlatos,
                                  BigDecimal sueldos, BigDecimal canon,
                                  LocalDate fechaDesde, LocalDate fechaHasta) {
        this(ventasTotales, costosPlatos, sueldos, canon);
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
    }

    public BigDecimal getVentasTotales() { return ventasTotales; }
    public BigDecimal getCostosPlatos() { return costosPlatos; }
    public BigDecimal getSueldos() { return sueldos; }
    public BigDecimal getCanon() { return canon; }
    public BigDecimal getRentabilidadNeta() { return rentabilidadNeta; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
}
