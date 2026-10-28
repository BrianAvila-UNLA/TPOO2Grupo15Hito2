package com.unla.grupo15.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class PuestoDesarmable extends UnidadVenta{

    private long cantidadCarpas;

    private long tiempoMontaje; //Tiene que ser en minutos

    @Override
    public BigDecimal calcularCanon(){
        BigDecimal costoSuperficie = festival.getCosto().getCostoPorSuperficie();
        BigDecimal costoMontaje = festival.getCosto().getCostoPorMontaje();

        BigDecimal superficie = BigDecimal.valueOf(getSuperficie());
        BigDecimal base = superficie.multiply(costoSuperficie);

        BigDecimal tiempo = BigDecimal.valueOf(getTiempoMontaje());
        BigDecimal montoMontaje = tiempo.multiply(costoMontaje);

        base = base.add(montoMontaje);

        return base;
    }

}

