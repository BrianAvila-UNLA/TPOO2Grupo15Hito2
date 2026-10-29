package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.AsignacionPersonal;
import com.unla.grupo15.entities.Personal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAsignacionPersonalRepository extends JpaRepository<AsignacionPersonal, Long> {

    @EntityGraph(attributePaths = {"unidadVenta", "festival", "festival.costo"})
    List<AsignacionPersonal> findByPersonalOrderByFechaInicioDesc(Personal personal);

    @EntityGraph(attributePaths = {"unidadVenta", "festival", "festival.costo"})
    Optional<AsignacionPersonal> findByIdAsignacionAndPersonal(long idAsignacion, Personal personal);

    Optional<AsignacionPersonal> findFirstByPersonalAndFechaFinIsNullOrderByFechaInicioDesc(Personal personal);
}
