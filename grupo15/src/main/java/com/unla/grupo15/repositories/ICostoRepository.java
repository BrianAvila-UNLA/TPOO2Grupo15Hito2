package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Costo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("costoRepository")
public interface ICostoRepository extends JpaRepository<Costo, Long> {

    public abstract Optional<Costo> findById(long id);

}
