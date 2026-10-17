package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class Costo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long idCosto;

    private BigDecimal costoPorSuperficie;

    private BigDecimal costoPorMontaje;

    private BigDecimal plusUsoElectricidad;

    private BigDecimal sueldoBase;

    private BigDecimal plusCocinero;

    private BigDecimal plusCocineroAyudante;

    private BigDecimal plusLavaplatos;

    public Costo(BigDecimal costoPorSuperficie, BigDecimal costoPorMontaje, BigDecimal plusUsoElectricidad,
                 BigDecimal sueldoBase, BigDecimal plusCocinero, BigDecimal plusCocineroAyudante, BigDecimal plusLavaplatos) {
        this.costoPorSuperficie = costoPorSuperficie;
        this.costoPorMontaje = costoPorMontaje;
        this.plusUsoElectricidad = plusUsoElectricidad;
        this.sueldoBase = sueldoBase;
        this.plusCocinero = plusCocinero;
        this.plusCocineroAyudante = plusCocineroAyudante;
        this.plusLavaplatos = plusLavaplatos;
    }
}
