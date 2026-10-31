package com.unla.grupo15.services;

import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.UnidadVenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import com.unla.grupo15.dto.PlatoDTO;

public interface IPedidoService {

    List<Pedido> getAll();

    Optional<Pedido> findById(long id);

    Pedido insertOrUpdate(Pedido pedido);

    void delete(long id);

    Pedido registrarPedido(Map<Long, Long> platosConCantidad, UnidadVenta unidadVenta);

    long countByUnidadVenta(UnidadVenta unidadVenta);
    BigDecimal sumarRecaudacionPorUnidad(UnidadVenta unidadVenta);

    BigDecimal sumarRecaudacionPorFestival(Festival festival);

    BigDecimal sumarCostoPlatosPorUnidad(UnidadVenta unidadVenta);

    BigDecimal sumarRecaudacionPorUnidadEntreFechas(UnidadVenta unidadVenta, LocalDate desde, LocalDate hasta);

    BigDecimal sumarCostoPlatosPorUnidadEntreFechas(UnidadVenta unidadVenta, LocalDate desde, LocalDate hasta);

    Optional<PlatoDTO> platoEstrella(UnidadVenta unidadVenta, Festival festival);
}
