package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("personalRepository")
public interface IPersonalRepository extends JpaRepository<Personal, Long> {

    public abstract Optional<Personal> findById(long id);

    Optional<Personal> findByCredencialesEmail(String email);

    boolean existsByDni(int dni);

}


