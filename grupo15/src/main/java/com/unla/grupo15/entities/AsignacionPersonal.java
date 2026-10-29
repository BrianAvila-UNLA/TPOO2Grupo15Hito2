package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AsignacionPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long idAsignacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_personal", nullable = false)
    private Personal personal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidad_venta", nullable = false)
    private UnidadVenta unidadVenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_festival", nullable = false)
    private Festival festival;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    public AsignacionPersonal(Personal personal, UnidadVenta unidadVenta, Festival festival, LocalDate fechaInicio) {
        this.personal = personal;
        this.unidadVenta = unidadVenta;
        this.festival = festival;
        this.fechaInicio = fechaInicio;
    }
}
