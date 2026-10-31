package com.unla.grupo15.services;

import com.unla.grupo15.entities.Plato;
import com.unla.grupo15.entities.UnidadVenta;

import java.util.List;
import java.util.Optional;

public interface IPlatoService {

    List<Plato> getAll();

    Optional<Plato> findById(long id);

    Plato insertOrUpdate(Plato plato);

    void delete(long id);

    public List<Plato> findByUnidadVenta(UnidadVenta unidadVenta);

    void inactivar(long id);

    void activar(long id);

}
