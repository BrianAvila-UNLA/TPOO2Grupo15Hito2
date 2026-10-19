package com.unla.grupo15.services;

import com.unla.grupo15.entities.UnidadVenta;

import java.util.List;
import java.util.Optional;

public interface IUnidadVentaService {

    List<UnidadVenta> getAll();

    Optional<UnidadVenta> findById(long id);

    UnidadVenta insertOrUpdate(UnidadVenta unidadVenta);

    void delete(long id);

}
