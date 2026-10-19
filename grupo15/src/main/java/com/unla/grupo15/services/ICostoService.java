package com.unla.grupo15.services;

import com.unla.grupo15.entities.Costo;

import java.util.Optional;

public interface ICostoService {

    Optional<Costo> findById(long id);

    Costo insertOrUpdate(Costo costo);

    void delete(long id);

}
