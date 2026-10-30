package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.Cajero;
import com.unla.grupo15.entities.DetallePedido;
import com.unla.grupo15.entities.Pedido;
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
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cajero")
public class CajeroController {

    private static final ZoneId APPLICATION_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

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
        if (cierreCajaService.estaCerrada(cajero.getUnidadVenta(), LocalDate.now(APPLICATION_ZONE))) {
            return "redirect:/empleado/inicio?error=caja-cerrada";
        }

        model.addAttribute("unidadVenta", cajero.getUnidadVenta());
        model.addAttribute("platos", platoRepository.findByUnidadVenta(cajero.getUnidadVenta()));
        model.addAttribute("personal", cajero);
        model.addAttribute("fechaHoraActual", ZonedDateTime.now(APPLICATION_ZONE));
        return "cajero/nuevo-pedido";
    }

    @PostMapping("/pedido/guardar")
    public void guardarPedido(
            @RequestParam Map<String, String> allParams,
            Principal principal,
            HttpServletResponse response) throws IOException {
        Cajero cajero = buscarCajero(principal.getName());

        if (cajero.getUnidadVenta() == null) {
            response.sendRedirect("/empleado/inicio?error=sin-unidad");
            return;
        }
        if (cierreCajaService.estaCerrada(cajero.getUnidadVenta(), LocalDate.now(APPLICATION_ZONE))) {
            response.sendRedirect("/empleado/inicio?error=caja-cerrada");
            return;
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
                    response.sendRedirect("/cajero/pedido/nuevo?error=cantidad-invalida");
                    return;
                }
            }
        }

        if (platosConCantidad.isEmpty()) {
            response.sendRedirect("/cajero/pedido/nuevo?error=sin-productos");
            return;
        }

        Pedido pedido = pedidoService.registrarPedido(platosConCantidad, cajero.getUnidadVenta());
        response.sendRedirect("/empleado/inicio?pedidoExitoso=true&pedidoId=" + pedido.getId());
    }

    @GetMapping("/pedido/pdf/{id}")
    public void descargarComprobantePedido(
            @PathVariable long id,
            Principal principal,
            HttpServletResponse response) throws IOException {
        Cajero cajero = buscarCajero(principal.getName());
        Pedido pedido = pedidoService.findById(id).orElse(null);

        if (pedido == null || pedido.getUnidadVenta() != cajero.getUnidadVenta()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Pedido no encontrado.");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=pedido_" + pedido.getId() + ".pdf");

        BigDecimal total = pedido.getDetalles().stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("--- EPICENTRO GOURMET ---"));
        document.add(new Paragraph("Comprobante de pedido"));
        document.add(new Paragraph("Fecha de pedido: " + pedido.getFechaPedido()));
        document.add(new Paragraph("Cajero: " + cajero.getNombre() + " " + cajero.getApellido()));
        document.add(new Paragraph("Unidad de venta: " + pedido.getUnidadVenta().getNombreComercial()));
        document.add(new Paragraph("------------------------------------------"));

        for (DetallePedido detalle : pedido.getDetalles()) {
            document.add(new Paragraph(detalle.getPlato().getNombrePlato()
                    + " | Cantidad: " + detalle.getCantidad()
                    + " | Subtotal: $" + detalle.getSubtotal()));
        }

        document.add(new Paragraph("------------------------------------------"));
        document.add(new Paragraph("Total: $" + total));
        document.close();
    }

    @PostMapping("/cerrar-caja")
    public String verCierreCaja(Model model, Principal principal, HttpServletResponse response) {
        Cajero cajero = buscarCajero(principal.getName());

        if (cajero.getUnidadVenta() == null) {
            return "redirect:/empleado/inicio?error=sin-unidad";
        }

        UnidadVenta miUnidad = cajero.getUnidadVenta();
        LocalDate fecha = LocalDate.now(APPLICATION_ZONE);
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