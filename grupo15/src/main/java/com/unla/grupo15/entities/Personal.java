package com.unla.grupo15.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    protected long idPersonal;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 50)
    protected String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false, length = 50)
    protected String apellido;

    @NotNull(message = "El DNI es obligatorio")
    @Min(value = 1, message = "El DNI debe ser válido")
    @Column(unique = true, nullable = false)
    protected int dni;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Column(unique = true, nullable = false)
    protected String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario_credenciales", referencedColumnName = "id")
    protected Usuario credenciales;

    @Column(nullable = false, columnDefinition = "boolean default false")
    protected boolean eliminado = false;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la actual")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    protected LocalDate fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    protected LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_venta")
    protected UnidadVenta unidadVenta;

    public Personal(String nombre, String apellido, int dni, String email,
                    Usuario credenciales, LocalDate fechaNacimiento, LocalDate fechaIngreso, UnidadVenta unidadVenta) {
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
