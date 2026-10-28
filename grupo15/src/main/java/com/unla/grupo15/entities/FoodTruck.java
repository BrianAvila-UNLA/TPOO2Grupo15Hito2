package com.unla.grupo15.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class FoodTruck extends UnidadVenta{

    private String patente;

    private boolean conexionElectrica;

    @Override
    public BigDecimal calcularCanon(){
        BigDecimal costoSuperficie = festival.getCosto().getCostoPorSuperficie();
        BigDecimal costoPlusElectricidad = festival.getCosto().getPlusUsoElectricidad();

        BigDecimal superficie = BigDecimal.valueOf(getSuperficie());
        BigDecimal base = superficie.multiply(costoSuperficie);

        if(conexionElectrica){
            base = base.add(costoPlusElectricidad);
        }

        return base;
    }

}
