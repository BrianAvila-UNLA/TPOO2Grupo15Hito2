package com.unla.grupo15.entities;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Locale;

@Entity
@Getter @Setter @NoArgsConstructor
public class FoodTruck extends UnidadVenta{

    public static final String FORMATO_PATENTE = "^(?:[A-Z]{2}[0-9]{3}[A-Z]{2}|[A-Z]{3}[0-9]{3})$";
    private static final String FORMATO_PATENTE_INGRESO =
            "(?i)^(?:[A-Z]{2}\\s*[0-9]{3}\\s*[A-Z]{2}|[A-Z]{3}\\s*[0-9]{3})$";
    private static final BigDecimal PLUS_CONEXION_ELECTRICA = new BigDecimal("2000");

    @NotBlank(message = "La patente es obligatoria para un Food Truck")
    @Pattern(regexp = FORMATO_PATENTE,
            message = "La patente debe tener formato Mercosur (AA123BB) o anterior (ABC123)")
    private String patente;

    private boolean conexionElectrica;

    public static boolean esPatenteValida(String patente) {
        return patente != null && patente.trim().matches(FORMATO_PATENTE_INGRESO);
    }

    public static String normalizarPatente(String patente) {
        return patente == null ? null : patente.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

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
