package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.*;
import com.unla.grupo15.services.IPersonalService;
import com.unla.grupo15.services.IPlatoService;
import com.unla.grupo15.services.IUnidadVentaService;
import com.unla.grupo15.services.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/responsable")
public class ResponsableController {

    private final IUsuarioService usuarioService;
    private final IUnidadVentaService unidadVentaService;
    private final IPlatoService platoService;
    private final IPersonalService personalService;

    public ResponsableController(IUsuarioService usuarioService, IUnidadVentaService unidadVentaService, IPlatoService platoService, IPersonalService personalService) {
        this.usuarioService = usuarioService;
        this.unidadVentaService = unidadVentaService;
        this.platoService = platoService;
        this.personalService = personalService;
    }

    @GetMapping
    public String panelResponsable(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);

        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

            if (unidadVenta != null) {
                model.addAttribute("unidadVenta", unidadVenta);
                List<Plato> platos = platoService.findByUnidadVenta(unidadVenta);
                model.addAttribute("platos", platos);

                boolean canonDisponible = unidadVenta.getFestival() != null
                        && unidadVenta.getFestival().getCosto() != null;
                model.addAttribute("canonDisponible", canonDisponible);

                if (canonDisponible) {
                    BigDecimal canon = unidadVenta.calcularCanon();
                    model.addAttribute("canon", canon);
                }
            }
        }

        return "usuario/responsable";
    }

    @GetMapping("/empleados")
    public String listarEmpleados(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

        if (unidadVenta != null) {
            Set<Personal> empleados = unidadVenta.getPersonal();
            model.addAttribute("empleados", empleados);
        }

        model.addAttribute("usuario", usuario);
        return "personal/empleados-lista";
    }

    @GetMapping("/empleados/cajero/nuevo")
    public String formNuevoCajero(Model model) {
        model.addAttribute("empleado", new Cajero());
        model.addAttribute("tipo", "cajero");
        return "personal/empleado-form";
    }

    @PostMapping("/empleados/cajero/guardar")
    public String guardarCajero(@Valid @ModelAttribute("empleado") Cajero empleado, BindingResult result, Model model, Authentication authentication) {
        LocalDate fechaLimite = LocalDate.now().minusYears(18);
        if (empleado.getFechaNacimiento() != null
                && !empleado.getFechaNacimiento().isBefore(fechaLimite)) {
            result.rejectValue(
                    "fechaNacimiento",
                    "cajero.fechaNacimiento.menorEdad",
                    "El cajero debe ser mayor de 18 años");
        }
        if (personalService.existsByDni(empleado.getDni())) {
            result.rejectValue(
                    "dni",
                    "cajero.dni.duplicado",
                    "Ya existe un empleado registrado con ese DNI");
        }

        if (result.hasErrors()) {
            model.addAttribute("tipo", "cajero");
            return "personal/empleado-form";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

        personalService.registrarEmpleado(empleado, unidadVenta);

        return "redirect:/responsable/empleados";
    }

    @GetMapping("/empleados/cocinero/nuevo")
    public String formNuevoCocinero(Model model) {
        model.addAttribute("empleado", new Cocinero());
        model.addAttribute("tipo", "cocinero");
        return "personal/empleado-form";
    }

    @PostMapping("/empleados/cocinero/guardar")
    public String guardarCocinero(@Valid @ModelAttribute("empleado") Cocinero empleado, BindingResult result, Model model, Authentication authentication) {
        LocalDate fechaLimite = LocalDate.now().minusYears(18);
        if (empleado.getFechaNacimiento() != null
                && !empleado.getFechaNacimiento().isBefore(fechaLimite)) {
            result.rejectValue(
                    "fechaNacimiento",
                    "cocinero.fechaNacimiento.menorEdad",
                    "El cocinero debe ser mayor de 18 años");
        }
        if (personalService.existsByDni(empleado.getDni())) {
            result.rejectValue(
                    "dni",
                    "cocinero.dni.duplicado",
                    "Ya existe un empleado registrado con ese DNI");
        }

        if (result.hasErrors()) {
            model.addAttribute("tipo", "cocinero");
            return "personal/empleado-form";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

        personalService.registrarEmpleado(empleado, unidadVenta);

        return "redirect:/responsable/empleados";
    }

    @GetMapping("/empleados/baja/{id}")
    public String bajaEmpleado(@PathVariable("id") Long id) {
        personalService.bajaLogica(id);
        return "redirect:/responsable/empleados";
    }

    @GetMapping("/empleados/activar/{id}")
    public String activarEmpleado(@PathVariable Long id) {
        personalService.darDeAlta(id);
        return "redirect:/responsable/empleados";
    }

}
