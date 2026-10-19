package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    public abstract Optional<Pedido> findById(long id);

}
