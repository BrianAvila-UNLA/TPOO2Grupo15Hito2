package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_cierre_caja_unidad_fecha",
        columnNames = {"id_unidad_venta", "fecha"}))
@Getter
@Setter
@NoArgsConstructor
public class CierreCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long idCierreCaja;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidad_venta", nullable = false)
    private UnidadVenta unidadVenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_personal", nullable = false)
    private Personal cajero;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalDateTime fechaHoraCierre;

    @Column(nullable = false)
    private long totalPedidos;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal recaudacionTotal;

    public CierreCaja(UnidadVenta unidadVenta, Personal cajero, LocalDate fecha,
                      LocalDateTime fechaHoraCierre, long totalPedidos, BigDecimal recaudacionTotal) {
        this.unidadVenta = unidadVenta;
        this.cajero = cajero;
        this.fecha = fecha;
        this.fechaHoraCierre = fechaHoraCierre;
        this.totalPedidos = totalPedidos;
        this.recaudacionTotal = recaudacionTotal;
    }
}
