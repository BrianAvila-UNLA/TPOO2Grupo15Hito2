package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String nombrePlato;

    private BigDecimal precioVenta;

    private BigDecimal costoProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_venta")
    protected UnidadVenta unidadVenta;

    @OneToMany(mappedBy = "plato", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetallePedido> detalles = new HashSet<>();

    public Plato(String nombrePlato, BigDecimal precioVenta, BigDecimal costoProduccion, UnidadVenta unidadVenta) {
        this.nombrePlato = nombrePlato;
        this.precioVenta = precioVenta;
        this.costoProduccion = costoProduccion;
        this.unidadVenta = unidadVenta;
    }
}
