package com.unla.grupo15.repositories;


import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.entities.Plato;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("platoRepository")
public interface IPlatoRepository extends JpaRepository<Plato, Long> {

    public abstract Optional<Plato> findById(long id);

    public abstract List<Plato> findByUnidadVenta(UnidadVenta unidadVenta);

}
