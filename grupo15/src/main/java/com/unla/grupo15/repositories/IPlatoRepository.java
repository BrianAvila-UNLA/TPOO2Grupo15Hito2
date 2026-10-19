package com.unla.grupo15.repositories;


import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("platoRepository")
public interface IPlatoRepository extends JpaRepository<Plato, Long> {

    public abstract Optional<Plato> findById(long id);

}
