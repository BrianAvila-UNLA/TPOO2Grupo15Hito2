package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.Cajero;
import com.unla.grupo15.entities.AsignacionPersonal;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.services.IPersonalService;
import com.unla.grupo15.services.implementation.LiquidacionService;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

@Controller
@RequestMapping("/empleado")
public class PersonalController {

    private final IPersonalService personalService;
    private final LiquidacionService liquidacionService;

    public PersonalController(IPersonalService personalService, LiquidacionService liquidacionService) {
        this.personalService = personalService;
        this.liquidacionService = liquidacionService;
    }

    @GetMapping("/inicio")
    public String panelEmpleado(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/usuarios/login";
        }

        model.addAttribute("liquidaciones", Collections.emptyList());
        Optional<Personal> personalOpt = personalService.findByCredencialesEmail(principal.getName());
        if (personalOpt.isPresent()) {
            cargarDatosPanel(model, personalOpt.get());
        } else {
            model.addAttribute("errorPersonal", "No se encontró una ficha de empleado asociada a esta cuenta.");
        }
        return "empleado/inicio";
    }

    @GetMapping("/liquidacion")
    public String verLiquidacion(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/usuarios/login";
        }

        Optional<Personal> personalOpt = personalService.findByCredencialesEmail(principal.getName());

        if (personalOpt.isPresent()) {
            Personal personal = personalOpt.get();
            model.addAttribute("personal", personal);
            model.addAttribute("liquidaciones", crearLiquidaciones(personal));
        } else {
            model.addAttribute("errorLiquidacion", "No se encontró información de personal asociada a este usuario.");
        }

        return "personal/liquidacion";
    }

    @GetMapping("/recibo/pdf/{idAsignacion}")
    public void descargarReciboPdf(@PathVariable long idAsignacion, Principal principal,
                                   HttpServletResponse response) throws IOException {
        if (principal == null) {
            response.sendRedirect("/usuarios/login");
            return;
        }

        Optional<Personal> personalOpt = personalService.findByCredencialesEmail(principal.getName());
        if (personalOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontró la ficha del empleado.");
            return;
        }

        Personal personal = personalOpt.get();
        Optional<AsignacionPersonal> asignacionOpt = personalService.findAsignacion(idAsignacion, personal);
        if (asignacionOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontró la asignación del empleado.");
            return;
        }

        AsignacionPersonal asignacion = asignacionOpt.get();
        UnidadVenta unidadVenta = asignacion.getUnidadVenta();
        Festival festival = asignacion.getFestival();
        final java.math.BigDecimal totalNeto;
        try {
            totalNeto = liquidacionService.calcularSueldo(personal, festival);
        } catch (IllegalStateException exception) {
            response.sendError(HttpServletResponse.SC_CONFLICT, exception.getMessage());
            return;
        }

        long diasFestival = java.time.temporal.ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin()) + 1;
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=recibo_sueldo_" + personal.getDni()
                + "_asignacion_" + asignacion.getIdAsignacion() + ".pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("--- EPICENTRO GOURMET ---"));
        document.add(new Paragraph("Recibo de Sueldo"));
        document.add(new Paragraph("Festival: " + festival.getNombre()));
        document.add(new Paragraph("Unidad de venta: " + unidadVenta.getNombreComercial()));
        document.add(new Paragraph("--------------------------------------------------"));
        document.add(new Paragraph("Empleado: " + personal.getNombre() + " " + personal.getApellido()));
        document.add(new Paragraph("DNI: " + personal.getDni()));
        document.add(new Paragraph("Período trabajado: " + asignacion.getFechaInicio()
                + (asignacion.getFechaFin() == null ? " - actualidad" : " - " + asignacion.getFechaFin())));
        document.add(new Paragraph("--------------------------------------------------"));
        document.add(new Paragraph("Días del festival: " + diasFestival));
        document.add(new Paragraph("Sueldo calculado: $" + totalNeto));
        document.close();
    }

    private void cargarDatosPanel(Model model, Personal personal) {
        model.addAttribute("personal", personal);
        model.addAttribute("esCajero", personal instanceof Cajero);
        model.addAttribute("liquidaciones", crearLiquidaciones(personal));
    }

    private List<Map<String, Object>> crearLiquidaciones(Personal personal) {
        List<Map<String, Object>> liquidaciones = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        for (AsignacionPersonal asignacion : personalService.obtenerHistorialAsignaciones(personal)) {
            Map<String, Object> liquidacion = new HashMap<>();
            liquidacion.put("asignacion", asignacion);
            Festival festival = asignacion.getFestival();
            LocalDate inicio = festival.getFechaInicio();
            LocalDate fin = festival.getFechaFin();
            boolean activo = inicio != null && fin != null
                    && !hoy.isBefore(inicio) && !hoy.isAfter(fin)
                    && !hoy.isBefore(asignacion.getFechaInicio())
                    && asignacion.getFechaFin() == null;
            liquidacion.put("activo", activo);
            try {
                liquidacion.put("sueldo", liquidacionService.calcularSueldo(personal, festival));
            } catch (IllegalStateException exception) {
                liquidacion.put("error", exception.getMessage());
            }
            liquidaciones.add(liquidacion);
        }
        return liquidaciones;
    }
}