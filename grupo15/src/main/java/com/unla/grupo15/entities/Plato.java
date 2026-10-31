package com.unla.grupo15.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "El nombre del plato es obligatorio")
    private String nombrePlato;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor que cero")
    private BigDecimal precioVenta;

    @NotNull(message = "El costo de producción es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo de producción debe ser mayor que cero")
    private BigDecimal costoProduccion;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean activo = true;

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
