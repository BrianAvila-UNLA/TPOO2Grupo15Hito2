package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Personal {

    @Column(nullable = false)
    private String especialidadCulinaria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolCocina categoria;

}
