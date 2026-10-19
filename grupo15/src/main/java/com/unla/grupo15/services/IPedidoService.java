package com.unla.grupo15.services;

import com.unla.grupo15.entities.Pedido;

import java.util.List;
import java.util.Optional;

public interface IPedidoService {

    List<Pedido> getAll();

    Optional<Pedido> findById(long id);

    Pedido insertOrUpdate(Pedido pedido);

    void delete(long id);

}
