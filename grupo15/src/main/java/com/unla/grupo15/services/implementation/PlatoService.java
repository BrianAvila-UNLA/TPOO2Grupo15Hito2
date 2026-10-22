package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Plato;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.repositories.IPlatoRepository;
import com.unla.grupo15.services.IPlatoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("platoService")
@SuppressWarnings("unused")
public class PlatoService implements IPlatoService {

    private final IPlatoRepository platoRepository;
    private final ModelMapper modelMapper;

    public PlatoService(IPlatoRepository platoRepository, ModelMapper modelMapper) {
        this.platoRepository = platoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Plato> getAll() {
        return platoRepository.findAll();
    }

    @Override
    public Optional<Plato> findById(long id) {
        return platoRepository.findById(id);
    }

    @Override
    public Plato insertOrUpdate(Plato plato) {
        return platoRepository.save(plato);
    }

    @Override
    public void delete(long id) {
        platoRepository.deleteById(id);
    }

    @Override
    public List<Plato> findByUnidadVenta(UnidadVenta unidadVenta) {
        return platoRepository.findByUnidadVenta(unidadVenta);
    }

}
