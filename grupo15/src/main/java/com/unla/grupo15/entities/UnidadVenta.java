package com.unla.grupo15.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    protected long idUnidadVenta;

    protected String nombreComercial;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    protected Usuario responsableCargo;

    protected long superficie;

    protected String codigoUnico;

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
        this.festival = festival;
    }
}
