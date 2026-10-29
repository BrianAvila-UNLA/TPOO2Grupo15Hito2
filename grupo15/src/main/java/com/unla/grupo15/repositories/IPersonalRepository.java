package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Personal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("personalRepository")
public interface IPersonalRepository extends JpaRepository<Personal, Long> {
    Optional<Personal> findByEmail(String email);

    public abstract Optional<Personal> findById(long id);

    @EntityGraph(attributePaths = {"unidadVenta", "unidadVenta.festival", "unidadVenta.festival.costo"})
    Optional<Personal> findByCredencialesEmail(String email);

    boolean existsByDni(int dni);

}


