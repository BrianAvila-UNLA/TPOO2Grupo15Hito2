package com.unla.grupo15.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {

    public static final String FORMATO_CODIGO_UNICO = "^[A-Z]{2}-[0-9]{7}$";
    public static final BigDecimal COSTO_POR_METRO_CUADRADO = new BigDecimal("500");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    protected long idUnidadVenta;

    protected String nombreComercial;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    protected Usuario responsableCargo;

    protected long superficie;

    @NotBlank(message = "El código único es obligatorio")
    @Pattern(regexp = FORMATO_CODIGO_UNICO,
            message = "El código debe tener el formato XX-0000000 (dos letras mayúsculas y siete dígitos)")
    @Column(length = 10, nullable = false, unique = true)
    protected String codigoUnico;

    protected LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_festival")
    protected Festival festival;

    @OneToMany(mappedBy = "unidadVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    protected Set<Plato> platos = new HashSet<>();

    @OneToMany(mappedBy = "unidadVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    protected Set<Personal> personal = new HashSet<>();

    @OneToMany(mappedBy = "unidadVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    protected Set<Pedido> pedidos = new HashSet<>();

    protected UnidadVenta(String nombreComercial, Usuario responsableCargo, long superficie, String codigoUnico) {
        this.nombreComercial = nombreComercial;
        this.responsableCargo = responsableCargo;
        this.superficie = superficie;
        this.codigoUnico = codigoUnico;
    }

    @PrePersist
    protected void establecerFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDate.now();
        }
    }

    public abstract BigDecimal calcularCanon();

}
