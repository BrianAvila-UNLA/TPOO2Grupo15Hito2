package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.Cajero;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.repositories.IPlatoRepository;
import com.unla.grupo15.services.IPedidoService;
import com.unla.grupo15.services.IPersonalService;
import com.unla.grupo15.services.implementation.CierreCajaService;
import com.unla.grupo15.services.implementation.CierreCajaYaRealizadoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cajero")
public class CajeroController {

    @Autowired
    private IPedidoService pedidoService;

    @Autowired
    private IPersonalService personalService;

    @Autowired
    private IPlatoRepository platoRepository;

    @Autowired
    private CierreCajaService cierreCajaService;

    @GetMapping("/inicio")
    public String panelEmpleado(Principal principal) {
        if (principal == null) {
            return "redirect:/usuarios/login";
        }

        buscarCajero(principal.getName());
        return "redirect:/empleado/inicio";
    }

    @GetMapping("/pedido/nuevo")
    public String nuevoPedido(Model model, Principal principal) {
        Cajero cajero = buscarCajero(principal.getName());
        if (cajero.getUnidadVenta() == null) {
            return "redirect:/empleado/inicio?error=sin-unidad";
        }
        if (cierreCajaService.estaCerrada(cajero.getUnidadVenta(), LocalDate.now())) {
            return "redirect:/empleado/inicio?error=caja-cerrada";
        }

        model.addAttribute("unidadVenta", cajero.getUnidadVenta());
        model.addAttribute("platos", platoRepository.findByUnidadVenta(cajero.getUnidadVenta()));
        return "cajero/nuevo-pedido";
    }

    @PostMapping("/pedido/guardar")
    public String guardarPedido(@RequestParam Map<String, String> allParams, Principal principal) {
        Cajero cajero = buscarCajero(principal.getName());

        if (cajero.getUnidadVenta() == null) {
            return "redirect:/empleado/inicio?error=sin-unidad";
        }
        if (cierreCajaService.estaCerrada(cajero.getUnidadVenta(), LocalDate.now())) {
            return "redirect:/empleado/inicio?error=caja-cerrada";
        }

        Map<Long, Long> platosConCantidad = new HashMap<>();

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("cantidades[")) {
                try {
                    Long idPlato = Long.valueOf(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
                    Long cantidad = Long.valueOf(entry.getValue());

                    if (cantidad > 0) {
                        platosConCantidad.put(idPlato, cantidad);
                    }
                } catch (NumberFormatException e) {
                    return "redirect:/cajero/pedido/nuevo?error=cantidad-invalida";
                }
            }
        }

        if (platosConCantidad.isEmpty()) {
            return "redirect:/cajero/pedido/nuevo?error=sin-productos";
        }

        pedidoService.registrarPedido(platosConCantidad, cajero.getUnidadVenta());
        return "redirect:/empleado/inicio?pedidoExitoso=true";
    }

    @PostMapping("/cerrar-caja")
    public String verCierreCaja(Model model, Principal principal, HttpServletResponse response) {
        Cajero cajero = buscarCajero(principal.getName());

        if (cajero.getUnidadVenta() == null) {
            return "redirect:/empleado/inicio?error=sin-unidad";
        }

        UnidadVenta miUnidad = cajero.getUnidadVenta();
        LocalDate fecha = LocalDate.now();
        try {
            model.addAttribute("cierreCaja", cierreCajaService.cerrarCaja(miUnidad, cajero, fecha));
            model.addAttribute("yaCerrada", false);
        } catch (CierreCajaYaRealizadoException exception) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            model.addAttribute("cierreCaja", cierreCajaService.buscar(miUnidad, fecha)
                    .orElseThrow(() -> exception));
            model.addAttribute("yaCerrada", true);
        }

        return "cajero/cierre-caja";
    }

    private Cajero buscarCajero(String emailUsuario) {
        Personal personal = personalService.findByCredencialesEmail(emailUsuario).orElse(null);
        if (personal instanceof Cajero cajero) {
            return cajero;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el personal asignado como cajero puede operar la caja.");
    }
}