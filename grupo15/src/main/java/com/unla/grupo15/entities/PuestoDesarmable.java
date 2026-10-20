package com.unla.grupo15.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class PuestoDesarmable extends UnidadVenta{

    private long cantidadCarpas;

    private long tiempoMontaje; //Tiene que ser en minutos

}

