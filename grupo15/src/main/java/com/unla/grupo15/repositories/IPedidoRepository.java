package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    public abstract Optional<Pedido> findById(long id);

    public abstract List<Pedido> findByUnidadVenta(UnidadVenta unidadVenta);

    @Query("SELECT SUM(dp.subtotal) FROM Pedido p JOIN p.detalles dp WHERE p.unidadVenta = :unidad")
    public abstract BigDecimal sumarRecaudacionPorUnidad(@Param("unidad") UnidadVenta unidad);

    long countByUnidadVentaAndFechaPedido(UnidadVenta unidadVenta, java.time.LocalDate fechaPedido);

    @Query("SELECT SUM(dp.subtotal) FROM Pedido p JOIN p.detalles dp "
            + "WHERE p.unidadVenta = :unidad AND p.fechaPedido = :fecha")
    BigDecimal sumarRecaudacionPorUnidadYFecha(
            @Param("unidad") UnidadVenta unidad,
            @Param("fecha") java.time.LocalDate fecha);

    public abstract long countByUnidadVenta(UnidadVenta unidadVenta);
}