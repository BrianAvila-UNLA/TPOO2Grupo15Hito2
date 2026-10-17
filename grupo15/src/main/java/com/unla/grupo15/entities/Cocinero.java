package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Personal {

    private String especialidadCulinaria;

    @Enumerated(EnumType.STRING)
    private RolCocina categoria;

}
