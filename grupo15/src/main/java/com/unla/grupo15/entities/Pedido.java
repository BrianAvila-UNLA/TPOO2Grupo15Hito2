package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private LocalDate fechaPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_venta")
    private UnidadVenta unidadVenta;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetallePedido> detalles = new HashSet<>();

    public Pedido(LocalDate fechaPedido, UnidadVenta unidadVenta) {
        this.fechaPedido = fechaPedido;
        this.unidadVenta = unidadVenta;
    }
}
