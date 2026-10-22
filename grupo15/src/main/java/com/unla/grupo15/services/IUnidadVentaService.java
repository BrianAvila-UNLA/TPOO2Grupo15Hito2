package com.unla.grupo15.services;

import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUnidadVentaService {

    List<UnidadVenta> getAll();

    Optional<UnidadVenta> findById(long id);

    UnidadVenta findByResponsableCargo(Usuario responsableCargo);

    UnidadVenta insertOrUpdate(UnidadVenta unidadVenta);

    void delete(long id);

}
