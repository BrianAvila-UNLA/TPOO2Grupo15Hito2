package com.unla.grupo15.dto;

import java.math.BigDecimal;

public class PlatoDTO {

    private final String nombre;
    private final BigDecimal precioVenta;
    private final long cantidadVendida;

    public PlatoDTO(String nombre, BigDecimal precioVenta, long cantidadVendida) {
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.cantidadVendida = cantidadVendida;
    }

    public String getNombre() { return nombre; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public long getCantidadVendida() { return cantidadVendida; }
}
