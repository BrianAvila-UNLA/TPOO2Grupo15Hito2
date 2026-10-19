package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("detallePedidoRepository")
public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    public abstract Optional<DetallePedido> findById(long id);

}
