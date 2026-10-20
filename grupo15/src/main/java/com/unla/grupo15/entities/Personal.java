package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    protected long idPersonal;

    protected String nombre;

    protected String apellido;

    protected int dni;

    protected String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario_credenciales", referencedColumnName = "id")
    protected Usuario credenciales;

    protected LocalDate fechaNacimiento;

    protected LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_venta")
    protected UnidadVenta unidadVenta;

    public Personal(String nombre, String apellido, int dni, String email,
                    Usuario credenciales, LocalDate fechaNacimiento, LocalDate fechaIngreso) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.credenciales = credenciales;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaIngreso = fechaIngreso;
        this.unidadVenta = unidadVenta;
    }
}
