package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.repositories.IPedidoRepository;
import com.unla.grupo15.services.IPedidoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("pedidoService")
@SuppressWarnings("unused")
public class PedidoService implements IPedidoService {

    private final IPedidoRepository pedidoRepository;

    public PedidoService(IPedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> findById(long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Pedido insertOrUpdate(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public void delete(long id) {
        pedidoRepository.deleteById(id);
    }

}
