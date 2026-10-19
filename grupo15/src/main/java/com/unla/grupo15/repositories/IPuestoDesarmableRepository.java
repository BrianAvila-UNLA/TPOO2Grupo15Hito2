package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.PuestoDesarmable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("puestoDesarmableRepository")
public interface IPuestoDesarmableRepository extends JpaRepository<PuestoDesarmable, Long> {

    public abstract Optional<PuestoDesarmable> findById(long id);

}
