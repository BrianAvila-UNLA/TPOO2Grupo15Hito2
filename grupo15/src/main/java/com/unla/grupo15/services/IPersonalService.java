package com.unla.grupo15.services;

import com.unla.grupo15.entities.Personal;

import java.util.List;
import java.util.Optional;

public interface IPersonalService {

    List<Personal> getAll();

    Optional<Personal> findById(long id);

    Personal insertOrUpdate(Personal personal);

    void delete(long id);

    Optional<Personal> findByEmail(String email);

}
