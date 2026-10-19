package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Cajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("cajeroRepository")
public interface ICajeroRepository extends JpaRepository<Cajero, Long> {

    public abstract Optional<Cajero> findById(long id);

}