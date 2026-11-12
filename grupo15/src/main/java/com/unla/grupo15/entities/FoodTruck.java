package com.unla.grupo15.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class FoodTruck extends UnidadVenta{

    private static final BigDecimal PLUS_CONEXION_ELECTRICA = new BigDecimal("2000");

    private String patente;

    private boolean conexionElectrica;

    @Override
    public BigDecimal calcularCanon(){
        BigDecimal superficie = BigDecimal.valueOf(getSuperficie());
        BigDecimal base = superficie.multiply(COSTO_POR_METRO_CUADRADO);

        if(conexionElectrica){
            base = base.add(PLUS_CONEXION_ELECTRICA);
        }

        return base;
    }

}
