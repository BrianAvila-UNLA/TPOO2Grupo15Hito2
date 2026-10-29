package com.unla.grupo15.services;

import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.entities.UnidadVenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IPedidoService {

    List<Pedido> getAll();

    Optional<Pedido> findById(long id);

    Pedido insertOrUpdate(Pedido pedido);

    void delete(long id);

    void registrarPedido(Map<Long, Long> platosConCantidad, UnidadVenta unidadVenta);

    long countByUnidadVenta(UnidadVenta unidadVenta);
    BigDecimal sumarRecaudacionPorUnidad(UnidadVenta unidadVenta);
}
