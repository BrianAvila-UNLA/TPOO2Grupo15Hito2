package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.repositories.IUnidadVentaRepository;
import com.unla.grupo15.services.IUnidadVentaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("unidadVentaService")
@SuppressWarnings("unused")
public class UnidadVentaService implements IUnidadVentaService {

    private final IUnidadVentaRepository unidadVentaRepository;
    private final ModelMapper modelMapper;

    public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository, ModelMapper modelMapper) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<UnidadVenta> getAll() {
        return unidadVentaRepository.findAll();
    }

    @Override
    public Optional<UnidadVenta> findById(long id) {
        return unidadVentaRepository.findById(id);
    }

    @Override
    public UnidadVenta insertOrUpdate(UnidadVenta unidadVenta) {
        return unidadVentaRepository.save(unidadVenta);
    }

    @Override
    public void delete(long id) {
        unidadVentaRepository.deleteById(id);
    }

    @Override
    public UnidadVenta findByResponsableCargo(Usuario responsableCargo) {
        return unidadVentaRepository.findByResponsableCargo(responsableCargo);
    }

}
