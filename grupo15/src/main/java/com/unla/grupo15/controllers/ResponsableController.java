package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.*;
import com.unla.grupo15.dto.ReporteVentaDTO;
import com.unla.grupo15.dto.ReporteRentabilidadDTO;
import com.unla.grupo15.services.IPersonalService;
import com.unla.grupo15.services.IPlatoService;
import com.unla.grupo15.services.IPedidoService;
import com.unla.grupo15.services.IUnidadVentaService;
import com.unla.grupo15.services.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.mail.MailException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/responsable")
public class ResponsableController {

    private final IUsuarioService usuarioService;
    private final IUnidadVentaService unidadVentaService;
    private final IPlatoService platoService;
    private final IPedidoService pedidoService;
    private final IPersonalService personalService;

    public ResponsableController(IUsuarioService usuarioService, IUnidadVentaService unidadVentaService,
            IPlatoService platoService, IPersonalService personalService,
            IPedidoService pedidoService) {
        this.usuarioService = usuarioService;
        this.unidadVentaService = unidadVentaService;
        this.platoService = platoService;
        this.personalService = personalService;
        this.pedidoService = pedidoService;
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

                Festival festival = unidadVenta.getFestival();
                if (festival != null) {
                    model.addAttribute("reporteVenta", new ReporteVentaDTO(
                            festival.getNombre(),
                            pedidoService.sumarRecaudacionPorUnidad(unidadVenta)));
                    model.addAttribute("platoEstrella",
                            pedidoService.platoEstrella(unidadVenta, festival).orElse(null));
                }

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

    @GetMapping("/reportes/rentabilidad")
    public String reporteRentabilidad(@RequestParam LocalDate fechaDesde,
            @RequestParam LocalDate fechaHasta,
            Model model, Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName()).orElse(null);
        UnidadVenta unidadVenta = usuario == null ? null : unidadVentaService.findByResponsableCargo(usuario);
        if (unidadVenta != null && unidadVenta.getFechaCreacion() != null
                && fechaDesde.isBefore(unidadVenta.getFechaCreacion())) {
            fechaDesde = unidadVenta.getFechaCreacion();
        }
        if (unidadVenta != null && unidadVenta.getFechaCreacion() != null
                && fechaHasta.isBefore(unidadVenta.getFechaCreacion())) {
            model.addAttribute("errorRentabilidad",
                    "La fecha hasta no puede ser anterior a la creación de la unidad de venta.");
            model.addAttribute("fechaDesde", unidadVenta.getFechaCreacion());
            model.addAttribute("fechaHasta", fechaHasta);
            model.addAttribute("fechaMinima", unidadVenta.getFechaCreacion());
            model.addAttribute("usuario", usuario);
            model.addAttribute("unidadVenta", unidadVenta);
            return "usuario/reportes";
        }
        cargarPanelConReporte(model, authentication, fechaDesde, fechaHasta);
        return "usuario/reportes";
    }

    @GetMapping("/reportes")
    public String reportes(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = usuario == null ? null : unidadVentaService.findByResponsableCargo(usuario);
        if (unidadVenta != null && unidadVenta.getFestival() != null) {
            Festival festival = unidadVenta.getFestival();
            LocalDate fechaDesde = festival.getFechaInicio();
            if (unidadVenta.getFechaCreacion() != null
                    && fechaDesde.isBefore(unidadVenta.getFechaCreacion())) {
                fechaDesde = unidadVenta.getFechaCreacion();
            }
            cargarPanelConReporte(model, authentication, fechaDesde, festival.getFechaFin());
        } else {
            model.addAttribute("usuario", usuario);
            model.addAttribute("unidadVenta", unidadVenta);
        }
        return "usuario/reportes";
    }

    private void cargarPanelConReporte(Model model, Authentication authentication,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = usuario == null ? null : unidadVentaService.findByResponsableCargo(usuario);
        if (unidadVenta == null || unidadVenta.getFestival() == null
                || fechaDesde == null || fechaHasta == null || fechaDesde.isAfter(fechaHasta)) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("unidadVenta", unidadVenta);
            if (unidadVenta != null) {
                model.addAttribute("fechaMinima", unidadVenta.getFechaCreacion());
            }
            return;
        }

        Festival festival = unidadVenta.getFestival();
        model.addAttribute("usuario", usuario);
        model.addAttribute("unidadVenta", unidadVenta);
        model.addAttribute("platos", platoService.findByUnidadVenta(unidadVenta));
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("fechaMinima", unidadVenta.getFechaCreacion());
        model.addAttribute("platoEstrella", pedidoService.platoEstrella(unidadVenta, festival).orElse(null));
        model.addAttribute("reporteVenta", new ReporteVentaDTO(
                festival.getNombre(),
                pedidoService.sumarRecaudacionPorUnidadEntreFechas(unidadVenta, fechaDesde, fechaHasta)));
        model.addAttribute("canonDisponible", festival.getCosto() != null);
        if (festival.getCosto() != null) {
            model.addAttribute("canon", unidadVenta.calcularCanon());
        }
        BigDecimal ventas = pedidoService.sumarRecaudacionPorUnidadEntreFechas(unidadVenta, fechaDesde, fechaHasta);
        BigDecimal costos = pedidoService.sumarCostoPlatosPorUnidadEntreFechas(unidadVenta, fechaDesde, fechaHasta);
        BigDecimal sueldos = personalService.calcularSueldos(unidadVenta, festival, fechaDesde, fechaHasta);
        model.addAttribute("reporteRentabilidad", new ReporteRentabilidadDTO(
                ventas, costos, sueldos, unidadVenta.calcularCanon(), fechaDesde, fechaHasta));
    }

    @GetMapping("/empleados")
    public String listarEmpleados(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

        if (unidadVenta != null) {
            Set<Personal> empleados = unidadVenta.getPersonal().stream()
                    .filter(empleado -> !empleado.isEliminado())
                    .collect(Collectors.toSet());
            model.addAttribute("empleados", empleados);
        }

        model.addAttribute("usuario", usuario);
        return "personal/empleados-lista";
    }

    @GetMapping("/menu")
    public String menu(Model model, Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName()).orElse(null);
        UnidadVenta unidadVenta = usuario == null ? null : unidadVentaService.findByResponsableCargo(usuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("unidadVenta", unidadVenta);
        model.addAttribute("platos", unidadVenta == null
                ? List.of()
                : platoService.findByUnidadVenta(unidadVenta));
        return "unidadVenta/menu";
    }

    @PostMapping("/menu/inactivar/{id}")
    public String inactivarPlato(@PathVariable("id") long id, Authentication authentication) {
        cambiarEstadoPlato(id, authentication, false);
        return "redirect:/responsable/menu";
    }

    @PostMapping("/menu/activar/{id}")
    public String activarPlato(@PathVariable("id") long id, Authentication authentication) {
        cambiarEstadoPlato(id, authentication, true);
        return "redirect:/responsable/menu";
    }

    private void cambiarEstadoPlato(long id, Authentication authentication, boolean activo) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName()).orElse(null);
        UnidadVenta unidadVenta = usuario == null ? null : unidadVentaService.findByResponsableCargo(usuario);
        Plato plato = platoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el plato"));
        if (unidadVenta == null || plato.getUnidadVenta() == null
                || plato.getUnidadVenta().getIdUnidadVenta() != unidadVenta.getIdUnidadVenta()) {
            throw new IllegalArgumentException("El plato no pertenece a la unidad de venta");
        }
        if (activo) {
            platoService.activar(id);
        } else {
            platoService.inactivar(id);
        }
    }

    @GetMapping("/empleados/cajero/nuevo")
    public String formNuevoCajero(Model model, Authentication authentication) {
        model.addAttribute("empleado", new Cajero());
        model.addAttribute("tipo", "cajero");
        model.addAttribute("modoEdicion", false);
        agregarUsuario(model, authentication);
        return "personal/empleado-form";
    }

    @PostMapping("/empleados/cajero/guardar")
    public String guardarCajero(@Valid @ModelAttribute("empleado") Cajero empleado, BindingResult result, Model model,
            Authentication authentication) {
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
        if (empleado.getEmail() != null && personalService.existsByEmail(empleado.getEmail())) {
            result.rejectValue(
                    "email",
                    "cajero.email.duplicado",
                    "Ya existe un empleado registrado con ese email");
        }

        if (result.hasErrors()) {
            model.addAttribute("tipo", "cajero");
            agregarUsuario(model, authentication);
            return "personal/empleado-form";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

        try {
            personalService.registrarEmpleado(empleado, unidadVenta);
        } catch (MailException exception) {
            model.addAttribute("tipo", "cajero");
            model.addAttribute("modoEdicion", false);
            agregarUsuario(model, authentication);
            model.addAttribute("error",
                    "No se pudo enviar el correo de credenciales. Verificá la configuración de correo e intentá nuevamente.");
            return "personal/empleado-form";
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("tipo", "cajero");
            model.addAttribute("modoEdicion", false);
            agregarUsuario(model, authentication);
            model.addAttribute("error",
                    "No se pudo registrar el empleado porque el DNI o email ya están registrados.");
            return "personal/empleado-form";
        }

        return "redirect:/responsable/empleados";
    }

    @GetMapping("/empleados/cocinero/nuevo")
    public String formNuevoCocinero(Model model, Authentication authentication) {
        model.addAttribute("empleado", new Cocinero());
        model.addAttribute("tipo", "cocinero");
        model.addAttribute("modoEdicion", false);
        agregarUsuario(model, authentication);
        return "personal/empleado-form";
    }

    @PostMapping("/empleados/cocinero/guardar")
    public String guardarCocinero(@Valid @ModelAttribute("empleado") Cocinero empleado, BindingResult result,
            Model model, Authentication authentication) {
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
        if (empleado.getEmail() != null && personalService.existsByEmail(empleado.getEmail())) {
            result.rejectValue(
                    "email",
                    "cocinero.email.duplicado",
                    "Ya existe un empleado registrado con ese email");
        }

        if (result.hasErrors()) {
            model.addAttribute("tipo", "cocinero");
            agregarUsuario(model, authentication);
            return "personal/empleado-form";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

        try {
            personalService.registrarEmpleado(empleado, unidadVenta);
        } catch (MailException exception) {
            model.addAttribute("tipo", "cocinero");
            model.addAttribute("modoEdicion", false);
            agregarUsuario(model, authentication);
            model.addAttribute("error",
                    "No se pudo enviar el correo de credenciales. Verificá la configuración de correo e intentá nuevamente.");
            return "personal/empleado-form";
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("tipo", "cocinero");
            model.addAttribute("modoEdicion", false);
            agregarUsuario(model, authentication);
            model.addAttribute("error",
                    "No se pudo registrar el empleado porque el DNI o email ya están registrados.");
            return "personal/empleado-form";
        }

        return "redirect:/responsable/empleados";
    }

    @GetMapping("/empleados/{id}/editar")
    public String formEditarEmpleado(@PathVariable Long id, Model model, Authentication authentication) {
        Personal empleado = buscarEmpleadoDeResponsable(id, authentication);
        if (empleado == null) {
            return "redirect:/responsable/empleados";
        }

        model.addAttribute("empleado", empleado);
        model.addAttribute("tipo", empleado instanceof Cocinero ? "cocinero" : "cajero");
        model.addAttribute("modoEdicion", true);
        agregarUsuario(model, authentication);
        return "personal/empleado-form";
    }

    @PostMapping("/empleados/{id}/editar/cajero")
    public String editarCajero(@PathVariable Long id,
            @Valid @ModelAttribute("empleado") Cajero datos,
            BindingResult result,
            Model model,
            Authentication authentication) {
        return editarEmpleado(id, datos, result, model, authentication, "cajero");
    }

    @PostMapping("/empleados/{id}/editar/cocinero")
    public String editarCocinero(@PathVariable Long id,
            @Valid @ModelAttribute("empleado") Cocinero datos,
            BindingResult result,
            Model model,
            Authentication authentication) {
        return editarEmpleado(id, datos, result, model, authentication, "cocinero");
    }

    private String editarEmpleado(Long id, Personal datos, BindingResult result, Model model,
            Authentication authentication, String tipo) {
        Personal empleado = buscarEmpleadoDeResponsable(id, authentication);
        if (empleado == null) {
            return "redirect:/responsable/empleados";
        }

        validarDatosEmpleado(datos, result, id, tipo);
        if (result.hasErrors()) {
            model.addAttribute("tipo", tipo);
            model.addAttribute("modoEdicion", true);
            agregarUsuario(model, authentication);
            return "personal/empleado-form";
        }

        empleado.setNombre(datos.getNombre());
        empleado.setApellido(datos.getApellido());
        empleado.setDni(datos.getDni());
        empleado.setEmail(datos.getEmail());
        empleado.setFechaNacimiento(datos.getFechaNacimiento());
        empleado.setFechaIngreso(datos.getFechaIngreso());
        if (empleado instanceof Cajero cajero && datos instanceof Cajero datosCajero) {
            cajero.setTurnoTrabajo(datosCajero.getTurnoTrabajo());
        } else if (empleado instanceof Cocinero cocinero && datos instanceof Cocinero datosCocinero) {
            cocinero.setEspecialidadCulinaria(datosCocinero.getEspecialidadCulinaria());
            cocinero.setCategoria(datosCocinero.getCategoria());
        }

        personalService.actualizarEmpleado(empleado);
        return "redirect:/responsable/empleados";
    }

    @PostMapping("/empleados/baja/{id}")
    public String bajaEmpleado(@PathVariable("id") Long id, Authentication authentication) {
        if (buscarEmpleadoDeResponsable(id, authentication) != null) {
            personalService.bajaLogica(id);
        }
        return "redirect:/responsable/empleados";
    }

    @PostMapping("/empleados/inactivar/{id}")
    public String inactivarEmpleado(@PathVariable("id") Long id, Authentication authentication) {
        if (buscarEmpleadoDeResponsable(id, authentication) != null) {
            personalService.inactivar(id);
        }
        return "redirect:/responsable/empleados";
    }

    @GetMapping("/empleados/activar/{id}")
    public String activarEmpleado(@PathVariable Long id, Authentication authentication) {
        if (buscarEmpleadoDeResponsable(id, authentication) != null) {
            personalService.darDeAlta(id);
        }
        return "redirect:/responsable/empleados";
    }

    private void validarDatosEmpleado(Personal empleado, org.springframework.validation.Errors result,
            long id, String tipo) {
        LocalDate fechaLimite = LocalDate.now().minusYears(18);
        if (empleado.getFechaNacimiento() != null
                && !empleado.getFechaNacimiento().isBefore(fechaLimite)) {
            result.rejectValue("fechaNacimiento", tipo + ".fechaNacimiento.menorEdad",
                    "El empleado debe ser mayor de 18 años");
        }

        if (personalService.existsByDniExcluding(empleado.getDni(), id)) {
            result.rejectValue("dni", tipo + ".dni.duplicado",
                    "Ya existe un empleado registrado con ese DNI");
        }
        if (personalService.existsByEmailExcluding(empleado.getEmail(), id)) {
            result.rejectValue("email", tipo + ".email.duplicado",
                    "Ya existe un empleado registrado con ese email");
        }
    }

    private Personal buscarEmpleadoDeResponsable(Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Usuario responsable = usuarioService.findByEmail(authentication.getName()).orElse(null);
        UnidadVenta unidadVenta = responsable == null
                ? null
                : unidadVentaService.findByResponsableCargo(responsable);
        if (unidadVenta == null) {
            return null;
        }
        return personalService.findById(id)
                .filter(empleado -> empleado.getUnidadVenta() != null
                        && empleado.getUnidadVenta().getIdUnidadVenta() == unidadVenta.getIdUnidadVenta())
                .orElse(null);
    }

    private void agregarUsuario(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            usuarioService.findByEmail(authentication.getName())
                    .ifPresent(usuario -> model.addAttribute("usuario", usuario));
        }
    }

}
