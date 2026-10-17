package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(unique = true, nullable = false, length = 45)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String contrasenia;

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    private boolean activo;

    public Usuario(String nombre, String contrasenia, RolUsuario rol, boolean activo) {
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.activo = activo;
    }

}
