package com.unla.grupo15.services;

import com.unla.grupo15.entities.DetallePedido;

import java.util.Optional;

public interface IDetallePedidoService {

    Optional<DetallePedido> findById(long id);

    DetallePedido insertOrUpdate(DetallePedido detallePedido);

    void delete(long id);

}
