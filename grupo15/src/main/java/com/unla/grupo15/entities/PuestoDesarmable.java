package com.unla.grupo15.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class PuestoDesarmable extends UnidadVenta{

    private static final BigDecimal DESCUENTO_POR_MINUTO_DE_MONTAJE = new BigDecimal("10");

    private long cantidadCarpas;

    private long tiempoMontaje; //Tiene que ser en minutos

    @Override
    public BigDecimal calcularCanon(){
        BigDecimal superficie = BigDecimal.valueOf(getSuperficie());
        BigDecimal costoSuperficie = superficie.multiply(COSTO_POR_METRO_CUADRADO);
        BigDecimal montoMontaje = BigDecimal.valueOf(getTiempoMontaje())
                .multiply(DESCUENTO_POR_MINUTO_DE_MONTAJE);
        return costoSuperficie.subtract(montoMontaje);
    }

}
