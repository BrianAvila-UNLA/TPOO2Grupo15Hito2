package com.unla.grupo15.services;

import com.unla.grupo15.entities.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioService {

    List<Usuario> getAll();

    Optional<Usuario> findById(long id);

    Optional<Usuario> findByEmail(String email);

    Usuario insertOrUpdate(Usuario usuario);

    void delete(long id);
}