package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("unidadVentaRepository")
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {

    public abstract Optional<UnidadVenta> findById(long id);

    public abstract UnidadVenta findByResponsableCargo(Usuario responsableCargo);

}
