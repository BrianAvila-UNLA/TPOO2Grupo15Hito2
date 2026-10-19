package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Costo;
import com.unla.grupo15.repositories.ICostoRepository;
import com.unla.grupo15.services.ICostoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("costoService")
@SuppressWarnings("unused")
public class CostoService implements ICostoService {

    private final ICostoRepository costoRepository;

    public CostoService(ICostoRepository costoRepository) {
        this.costoRepository = costoRepository;
    }

    @Override
    public Optional<Costo> findById(long id) {
        return costoRepository.findById(id);
    }

    @Override
    public Costo insertOrUpdate(Costo costo) {
        return costoRepository.save(costo);
    }

    @Override
    public void delete(long id) {
        costoRepository.deleteById(id);
    }

}
