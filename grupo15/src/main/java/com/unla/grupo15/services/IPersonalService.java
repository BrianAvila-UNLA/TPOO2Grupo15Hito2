package com.unla.grupo15.services;

import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.AsignacionPersonal;

import java.util.List;
import java.util.Optional;

public interface IPersonalService {

    List<Personal> getAll();

    Optional<Personal> findById(long id);

    Personal insertOrUpdate(Personal personal);

    void delete(long id);

    Optional<Personal> findByEmail(String email);

    Optional<Personal> findByCredencialesEmail(String email);

    List<AsignacionPersonal> obtenerHistorialAsignaciones(Personal personal);

    Optional<AsignacionPersonal> findAsignacion(long idAsignacion, Personal personal);

    public boolean esCocinero(String email);
}
