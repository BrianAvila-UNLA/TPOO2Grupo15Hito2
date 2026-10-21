package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("usuarioRepository")
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    public abstract Optional<Usuario> findById(long id);

    boolean existsByNombre(String nombre);

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);
}
