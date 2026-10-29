package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.CierreCaja;
import com.unla.grupo15.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ICierreCajaRepository extends JpaRepository<CierreCaja, Long> {

    boolean existsByUnidadVentaAndFecha(UnidadVenta unidadVenta, LocalDate fecha);

    Optional<CierreCaja> findByUnidadVentaAndFecha(UnidadVenta unidadVenta, LocalDate fecha);
}
