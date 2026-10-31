package com.unla.grupo15.services;

import com.unla.grupo15.entities.AsignacionPersonal;
import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Festival;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

public interface IPersonalService {

    List<Personal> getAll();

    Optional<Personal> findById(long id);

    Personal insertOrUpdate(Personal personal);

    void delete(long id);

    Optional<Personal> findByEmail(String email);

    boolean existsByDni(int dni);

    boolean existsByDniExcluding(int dni, long idPersonal);

    boolean existsByEmail(String email);

    boolean existsByEmailExcluding(String email, long idPersonal);

    void registrarEmpleado(Personal empleado, UnidadVenta unidadVenta);

    Personal actualizarEmpleado(Personal empleado);

    void bajaLogica(Long id);

    void inactivar(Long id);

    void darDeAlta(Long id);

    Optional<Personal> findByCredencialesEmail(String email);

    List<AsignacionPersonal> obtenerHistorialAsignaciones(Personal personal);

    Optional<AsignacionPersonal> findAsignacion(long idAsignacion, Personal personal);

    boolean esCocinero(String email);

    BigDecimal calcularSueldos(UnidadVenta unidadVenta, Festival festival);

    BigDecimal calcularSueldos(UnidadVenta unidadVenta, Festival festival,
                               LocalDate desde, LocalDate hasta);
}
