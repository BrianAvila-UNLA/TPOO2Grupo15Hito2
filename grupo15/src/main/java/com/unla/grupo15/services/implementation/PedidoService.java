package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.DetallePedido;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.entities.Plato;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.dto.PlatoDTO;
import com.unla.grupo15.repositories.IPedidoRepository;
import com.unla.grupo15.repositories.IPlatoRepository; // Asegúrate de inyectar tu repositorio de platos
import com.unla.grupo15.services.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;

@Service("pedidoService")
@SuppressWarnings("unused")
public class PedidoService implements IPedidoService {

    private final IPedidoRepository pedidoRepository;

    @Autowired
    private IPlatoRepository platoRepository;

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

    @Override
    @Transactional
    public Pedido registrarPedido(Map<Long, Long> platosConCantidad, UnidadVenta unidadVenta) {
        Pedido pedido = new Pedido(LocalDate.now(), unidadVenta);

        for (Map.Entry<Long, Long> entry : platosConCantidad.entrySet()) {
            Long idPlato = entry.getKey();
            Long cantidad = entry.getValue();

            if (cantidad != null && cantidad > 0) {
                Plato plato = platoRepository.findById(idPlato).orElse(null);

                if (plato != null) {
                    BigDecimal subtotal = plato.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad));

                    DetallePedido detalle = new DetallePedido(cantidad, subtotal, plato);
                    detalle.setPedido(pedido);

                    pedido.getDetalles().add(detalle);
                }
            }
        }

        if (!pedido.getDetalles().isEmpty()) {
            return pedidoRepository.save(pedido);
        }
        throw new IllegalArgumentException("No se pudo registrar un pedido sin productos.");
    }

    @Override
    public long countByUnidadVenta(UnidadVenta unidadVenta) {
        return pedidoRepository.countByUnidadVenta(unidadVenta);
    }

    @Override
    public BigDecimal sumarRecaudacionPorUnidad(UnidadVenta unidadVenta) {
        BigDecimal recaudacion = pedidoRepository.sumarRecaudacionPorUnidad(unidadVenta);
        return recaudacion == null ? BigDecimal.ZERO : recaudacion;
    }

    @Override
    public BigDecimal sumarRecaudacionPorFestival(Festival festival) {
        BigDecimal recaudacion = pedidoRepository.sumarRecaudacionPorFestival(festival);
        return recaudacion == null ? BigDecimal.ZERO : recaudacion;
    }

    @Override
    public BigDecimal sumarCostoPlatosPorUnidad(UnidadVenta unidadVenta) {
        BigDecimal costos = pedidoRepository.sumarCostoPlatosPorUnidad(unidadVenta);
        return costos == null ? BigDecimal.ZERO : costos;
    }

    @Override
    public BigDecimal sumarRecaudacionPorUnidadEntreFechas(UnidadVenta unidadVenta, LocalDate desde, LocalDate hasta) {
        BigDecimal total = pedidoRepository.sumarRecaudacionPorUnidadEntreFechas(unidadVenta, desde, hasta);
        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    public BigDecimal sumarCostoPlatosPorUnidadEntreFechas(UnidadVenta unidadVenta, LocalDate desde, LocalDate hasta) {
        BigDecimal total = pedidoRepository.sumarCostoPlatosPorUnidadEntreFechas(unidadVenta, desde, hasta);
        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    public Optional<PlatoDTO> platoEstrella(UnidadVenta unidadVenta, Festival festival) {
        return pedidoRepository.buscarPlatosMasVendidos(unidadVenta, festival, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(row -> new PlatoDTO((String) row[0], (BigDecimal) row[1], ((Number) row[2]).longValue()));
    }
}
