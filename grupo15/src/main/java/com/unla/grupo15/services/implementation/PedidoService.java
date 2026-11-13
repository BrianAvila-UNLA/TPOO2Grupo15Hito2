package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.DetallePedido;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.entities.Plato;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.dto.PlatoDTO;
import com.unla.grupo15.repositories.IPedidoRepository;
import com.unla.grupo15.repositories.IPlatoRepository;
import com.unla.grupo15.services.IPedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

@Service("pedidoService")
@SuppressWarnings("unused")
public class PedidoService implements IPedidoService {

    private static final ZoneId APPLICATION_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private final IPedidoRepository pedidoRepository;
    private final IPlatoRepository platoRepository;

    public PedidoService(IPedidoRepository pedidoRepository, IPlatoRepository platoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.platoRepository = platoRepository;
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
    @Transactional
    public Pedido insertOrUpdate(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio.");
        }
        if (pedido.getUnidadVenta() == null) {
            throw new IllegalArgumentException("La unidad de venta es obligatoria para registrar el pedido.");
        }
        if (pedido.getId() == null || pedido.getFestival() == null) {
            pedido.setFestival(pedido.getUnidadVenta().getFestival());
        }
        if (pedido.getFestival() == null) {
            throw new IllegalArgumentException("La unidad de venta debe estar asignada a un festival.");
        }
        validarFechaPedido(pedido.getFechaPedido());
        validarFechaDentroDelFestival(pedido.getFechaPedido(), pedido.getFestival());
        validarDetalles(pedido);
        return pedidoRepository.save(pedido);
    }

    @Override
    public void delete(long id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Pedido registrarPedido(Map<Long, Long> platosConCantidad, UnidadVenta unidadVenta) {
        if (unidadVenta == null) {
            throw new IllegalArgumentException("La unidad de venta es obligatoria para registrar el pedido.");
        }
        if (platosConCantidad == null) {
            throw new IllegalArgumentException("Los productos del pedido son obligatorios.");
        }
        Festival festival = unidadVenta.getFestival();
        if (festival == null) {
            throw new IllegalArgumentException("La unidad de venta debe estar asignada a un festival.");
        }

        LocalDate fechaPedido = LocalDate.now(APPLICATION_ZONE);
        validarFechaDentroDelFestival(fechaPedido, festival);
        Pedido pedido = new Pedido(fechaPedido, unidadVenta, festival);

        for (Map.Entry<Long, Long> entry : platosConCantidad.entrySet()) {
            Long idPlato = entry.getKey();
            Long cantidad = entry.getValue();

            if (cantidad == null || cantidad < 0) {
                throw new IllegalArgumentException("La cantidad de cada producto debe ser mayor o igual a cero.");
            }
            if (cantidad == 0) {
                continue;
            }

            Plato plato = idPlato == null
                    ? null
                    : platoRepository.findById(idPlato).orElse(null);
            validarPlato(plato, unidadVenta);

            BigDecimal subtotal = plato.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad));
            DetallePedido detalle = new DetallePedido(cantidad, subtotal, plato);
            detalle.setPedido(pedido);
            pedido.getDetalles().add(detalle);
        }

        if (!pedido.getDetalles().isEmpty()) {
            return pedidoRepository.save(pedido);
        }
        throw new IllegalArgumentException("No se pudo registrar un pedido sin productos.");
    }

    private void validarFechaPedido(LocalDate fechaPedido) {
        if (fechaPedido == null || !fechaPedido.equals(LocalDate.now(APPLICATION_ZONE))) {
            throw new IllegalArgumentException("La fecha del pedido debe ser la fecha actual.");
        }
    }

    private void validarFechaDentroDelFestival(LocalDate fechaPedido, Festival festival) {
        if (!festival.contieneFecha(fechaPedido)) {
            throw new IllegalArgumentException(
                    "Solo se pueden registrar pedidos durante las fechas del festival.");
        }
    }

    private void validarDetalles(Pedido pedido) {
        if (pedido == null || pedido.getUnidadVenta() == null
                || pedido.getFestival() == null
                || pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido debe tener una unidad de venta, un festival y al menos un producto.");
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad de cada producto debe ser mayor que cero.");
            }
            validarPlato(detalle.getPlato(), pedido.getUnidadVenta());
        }
    }

    private void validarPlato(Plato plato, UnidadVenta unidadVenta) {
        if (plato == null || !plato.isActivo() || plato.getUnidadVenta() == null
                || plato.getUnidadVenta().getIdUnidadVenta() != unidadVenta.getIdUnidadVenta()) {
            throw new IllegalArgumentException(
                    "Todos los platos del pedido deben estar activos y pertenecer a la unidad de venta.");
        }
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
    public BigDecimal sumarRecaudacionPorUnidadYFestival(UnidadVenta unidadVenta, Festival festival) {
        BigDecimal recaudacion = pedidoRepository.sumarRecaudacionPorUnidadYFestival(unidadVenta, festival);
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
    public BigDecimal sumarRecaudacionPorUnidadEntreFechas(
            UnidadVenta unidadVenta, Festival festival, LocalDate desde, LocalDate hasta) {
        BigDecimal total = pedidoRepository.sumarRecaudacionPorUnidadEntreFechas(
                unidadVenta, festival, desde, hasta);
        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    public BigDecimal sumarCostoPlatosPorUnidadEntreFechas(
            UnidadVenta unidadVenta, Festival festival, LocalDate desde, LocalDate hasta) {
        BigDecimal total = pedidoRepository.sumarCostoPlatosPorUnidadEntreFechas(
                unidadVenta, festival, desde, hasta);
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
