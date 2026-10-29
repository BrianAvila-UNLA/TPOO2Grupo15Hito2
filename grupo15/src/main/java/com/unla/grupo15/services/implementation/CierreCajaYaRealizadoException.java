package com.unla.grupo15.services.implementation;

public class CierreCajaYaRealizadoException extends RuntimeException {

    public CierreCajaYaRealizadoException() {
        super("La caja de esta unidad ya fue cerrada para la fecha seleccionada.");
    }

    public CierreCajaYaRealizadoException(Throwable cause) {
        super("La caja de esta unidad ya fue cerrada para la fecha seleccionada.", cause);
    }
}
