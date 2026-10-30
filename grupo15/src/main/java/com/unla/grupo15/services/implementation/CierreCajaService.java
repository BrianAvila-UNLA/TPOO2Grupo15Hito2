package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.CierreCaja;
import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.repositories.ICierreCajaRepository;
import com.unla.grupo15.repositories.IPedidoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class CierreCajaService {

    private static final ZoneId APPLICATION_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private final ICierreCajaRepository cierreCajaRepository;
    private final IPedidoRepository pedidoRepository;

    public CierreCajaService(ICierreCajaRepository cierreCajaRepository, IPedidoRepository pedidoRepository) {
        this.cierreCajaRepository = cierreCajaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public CierreCaja cerrarCaja(UnidadVenta unidadVenta, Personal cajero, LocalDate fecha) {
        if (cierreCajaRepository.existsByUnidadVentaAndFecha(unidadVenta, fecha)) {
            throw new CierreCajaYaRealizadoException();
        }

        long totalPedidos = pedidoRepository.countByUnidadVentaAndFechaPedido(unidadVenta, fecha);
        BigDecimal recaudacionTotal = pedidoRepository.sumarRecaudacionPorUnidadYFecha(unidadVenta, fecha);
        if (recaudacionTotal == null) {
            recaudacionTotal = BigDecimal.ZERO;
        }

        try {
            return cierreCajaRepository.saveAndFlush(new CierreCaja(
                    unidadVenta, cajero, fecha, LocalDateTime.now(APPLICATION_ZONE),
                    totalPedidos, recaudacionTotal));
        } catch (DataIntegrityViolationException exception) {
            if (cierreCajaRepository.existsByUnidadVentaAndFecha(unidadVenta, fecha)) {
                throw new CierreCajaYaRealizadoException(exception);
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public Optional<CierreCaja> buscar(UnidadVenta unidadVenta, LocalDate fecha) {
        return cierreCajaRepository.findByUnidadVentaAndFecha(unidadVenta, fecha);
    }

    @Transactional(readOnly = true)
    public boolean estaCerrada(UnidadVenta unidadVenta, LocalDate fecha) {
        return cierreCajaRepository.existsByUnidadVentaAndFecha(unidadVenta, fecha);
    }
}
