package com.unla.grupo15.services;

import com.unla.grupo15.entities.Festival;

import java.util.List;
import java.util.Optional;

public interface IFestivalService {

    List<Festival> getAll();

    Optional<Festival> findById(long id);

    Festival insertOrUpdate(Festival festival);

    void delete(long id);

}
