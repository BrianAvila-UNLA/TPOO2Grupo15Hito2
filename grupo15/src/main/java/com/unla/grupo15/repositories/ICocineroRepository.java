package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Cocinero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("cocineroRepository")
public interface ICocineroRepository extends JpaRepository<Cocinero, Long> {
    public abstract Optional<Cocinero> findById(long id);
}
