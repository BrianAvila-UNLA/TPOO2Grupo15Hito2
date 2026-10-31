package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    public abstract Optional<Pedido> findById(long id);

    public abstract List<Pedido> findByUnidadVenta(UnidadVenta unidadVenta);

    @Query("SELECT SUM(dp.subtotal) FROM Pedido p JOIN p.detalles dp WHERE p.unidadVenta = :unidad")
    public abstract BigDecimal sumarRecaudacionPorUnidad(@Param("unidad") UnidadVenta unidad);

    @Query("SELECT SUM(dp.subtotal) FROM Pedido p JOIN p.detalles dp "
            + "WHERE p.unidadVenta.festival = :festival")
    BigDecimal sumarRecaudacionPorFestival(@Param("festival") Festival festival);

    @Query("SELECT SUM(dp.cantidad * dp.plato.costoProduccion) FROM Pedido p JOIN p.detalles dp "
            + "WHERE p.unidadVenta = :unidad")
    BigDecimal sumarCostoPlatosPorUnidad(@Param("unidad") UnidadVenta unidad);

    @Query("SELECT SUM(dp.subtotal) FROM Pedido p JOIN p.detalles dp "
            + "WHERE p.unidadVenta = :unidad AND p.fechaPedido BETWEEN :desde AND :hasta")
    BigDecimal sumarRecaudacionPorUnidadEntreFechas(
            @Param("unidad") UnidadVenta unidad, @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    @Query("SELECT SUM(dp.cantidad * dp.plato.costoProduccion) FROM Pedido p JOIN p.detalles dp "
            + "WHERE p.unidadVenta = :unidad AND p.fechaPedido BETWEEN :desde AND :hasta")
    BigDecimal sumarCostoPlatosPorUnidadEntreFechas(
            @Param("unidad") UnidadVenta unidad, @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    @Query("SELECT dp.plato.nombrePlato, dp.plato.precioVenta, SUM(dp.cantidad) "
            + "FROM Pedido p JOIN p.detalles dp "
            + "WHERE p.unidadVenta = :unidad AND p.unidadVenta.festival = :festival "
            + "GROUP BY dp.plato.id, dp.plato.nombrePlato, dp.plato.precioVenta "
            + "ORDER BY SUM(dp.cantidad) DESC")
    List<Object[]> buscarPlatosMasVendidos(
            @Param("unidad") UnidadVenta unidad, @Param("festival") Festival festival,
            Pageable pageable);

    long countByUnidadVentaAndFechaPedido(UnidadVenta unidadVenta, java.time.LocalDate fechaPedido);

    @Query("SELECT SUM(dp.subtotal) FROM Pedido p JOIN p.detalles dp "
            + "WHERE p.unidadVenta = :unidad AND p.fechaPedido = :fecha")
    BigDecimal sumarRecaudacionPorUnidadYFecha(
            @Param("unidad") UnidadVenta unidad,
            @Param("fecha") java.time.LocalDate fecha);

    public abstract long countByUnidadVenta(UnidadVenta unidadVenta);
}