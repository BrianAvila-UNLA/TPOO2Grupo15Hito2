package com.unla.grupo15.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long idFestival;

    private String nombre;

    private String temporada;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_costo")
    private Costo costo;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy ="festival")
    private Set<UnidadVenta> unidadesVenta = new HashSet<>();

    public Festival(String nombre, String temporada, LocalDate fechaInicio, LocalDate fechaFin, Costo costo) {
        this.nombre = nombre;
        this.temporada = temporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costo = costo;
    }

    @AssertTrue(message = "La fecha de fin debe ser igual o posterior a la fecha de inicio")
    public boolean isPeriodoValido() {
        return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
    }
}
