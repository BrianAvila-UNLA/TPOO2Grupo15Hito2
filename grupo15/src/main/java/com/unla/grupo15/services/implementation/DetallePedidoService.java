package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.DetallePedido;
import com.unla.grupo15.repositories.IDetallePedidoRepository;
import com.unla.grupo15.services.IDetallePedidoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("detallePedidoService")
@SuppressWarnings("unused")
public class DetallePedidoService implements IDetallePedidoService {

    private final IDetallePedidoRepository detallePedidoRepository;

    public DetallePedidoService(IDetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Override
    public Optional<DetallePedido> findById(long id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    public DetallePedido insertOrUpdate(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    }

    @Override
    public void delete(long id) {
        detallePedidoRepository.deleteById(id);
    }

}
