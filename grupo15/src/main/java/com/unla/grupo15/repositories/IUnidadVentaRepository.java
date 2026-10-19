package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("PedidoRepository")
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {

    public abstract Optional<UnidadVenta> findById(long id);

}
