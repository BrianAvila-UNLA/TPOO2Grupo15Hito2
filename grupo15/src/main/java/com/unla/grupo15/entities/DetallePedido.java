package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_plato")
    private Plato plato;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    private long cantidad;

    private BigDecimal subtotal;

    public DetallePedido(long cantidad, BigDecimal subtotal, Plato plato) {
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.plato = plato;
    }
}
