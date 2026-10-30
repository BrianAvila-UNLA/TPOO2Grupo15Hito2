package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.FoodTruck;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.PuestoDesarmable;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.services.IFestivalService;
import com.unla.grupo15.services.IUnidadVentaService;
import com.unla.grupo15.services.UnidadVentaRegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/unidades")
public class UnidadVentaController {

    private final IUnidadVentaService unidadVentaService;
    private final UnidadVentaRegistrationService registrationService;
    private final IFestivalService festivalService;

    public UnidadVentaController(
            IUnidadVentaService unidadVentaService,
            UnidadVentaRegistrationService registrationService,
            IFestivalService festivalService) {
        this.unidadVentaService = unidadVentaService;
        this.registrationService = registrationService;
        this.festivalService = festivalService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("unidades", unidadVentaService.getAll());
        return "unidadVenta/index";
    }

    @GetMapping("/registrar")
    public String registrar(Model model) {
        model.addAttribute("registro", new RegistroUnidadForm());
        model.addAttribute("festivales", festivalService.getAll());
        return "unidadVenta/registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("registro") RegistroUnidadForm form,
                            BindingResult result,
                            Model model) {
        model.addAttribute("festivales", festivalService.getAll());
        if ("PUESTO".equals(form.tipo)
                && (form.cantidadCarpas <= 0 || form.tiempoMontaje <= 0)) {
            result.reject(
                    "registro.puesto.campos.invalidos",
                    "La cantidad de carpas y el tiempo de montaje deben ser mayores que cero para un puesto desarmable.");
        }

        Festival festival = form.festivalId == null
                ? null
                : festivalService.findById(form.festivalId).orElse(null);
        if (festival == null) {
            result.rejectValue("festivalId", "registro.festival.invalido",
                    "Seleccioná un festival válido.");
        }

        if (result.hasErrors()) {
            return "unidadVenta/registrar";
        }

        try {
            registrationService.register(new UnidadVentaRegistrationService.UnitRegistration(
                    form.tipo,
                    form.nombreComercial,
                    form.superficie,
                    form.codigoUnico,
                    form.email,
                    form.patente,
                    form.conexionElectrica,
                    form.cantidadCarpas,
                    form.tiempoMontaje,
                    festival));
            return "redirect:/unidades/registrar?enviado";
        } catch (IllegalArgumentException | org.springframework.mail.MailException exception) {
            String message = exception.getMessage();
            model.addAttribute("error", message == null || message.isBlank()
                    ? "No se pudo registrar la unidad. Verificá los datos e intentá nuevamente."
                    : message);
            return "unidadVenta/registrar";
        }
    }

    @GetMapping("/nuevo/foodtruck")
    public String nuevoFoodTruck(Model model) {
        model.addAttribute("unidadVenta", new FoodTruck());
        return "unidadVenta/nuevo-foodtruck";
    }

    @GetMapping("/nuevo/puesto")
    public String nuevoPuesto(Model model) {
        model.addAttribute("unidadVenta", new PuestoDesarmable());
        return "unidadVenta/nuevo-puesto";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("unidadVenta") UnidadVenta unidadVenta) {
        unidadVentaService.insertOrUpdate(unidadVenta);
        return "redirect:/unidades";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") long id) {
        unidadVentaService.delete(id);
        return "redirect:/unidades";
    }

    public static class RegistroUnidadForm {
        private String tipo = "FOODTRUCK";
        @jakarta.validation.constraints.NotBlank(message = "El nombre comercial es obligatorio")
        private String nombreComercial;
        @jakarta.validation.constraints.Min(value = 1, message = "La superficie debe ser mayor que cero")
        private long superficie;
        @jakarta.validation.constraints.NotBlank(message = "El código único es obligatorio")
        private String codigoUnico;
        @jakarta.validation.constraints.NotBlank(message = "El correo del responsable es obligatorio")
        @jakarta.validation.constraints.Email(message = "El correo del responsable no es válido")
        private String email;
        private String patente;
        private boolean conexionElectrica;
        private long cantidadCarpas;
        private long tiempoMontaje;
        @jakarta.validation.constraints.NotNull(message = "El festival es obligatorio")
        private Long festivalId;

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getNombreComercial() { return nombreComercial; }
        public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
        public long getSuperficie() { return superficie; }
        public void setSuperficie(long superficie) { this.superficie = superficie; }
        public String getCodigoUnico() { return codigoUnico; }
        public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPatente() { return patente; }
        public void setPatente(String patente) { this.patente = patente; }
        public boolean isConexionElectrica() { return conexionElectrica; }
        public void setConexionElectrica(boolean conexionElectrica) { this.conexionElectrica = conexionElectrica; }
        public long getCantidadCarpas() { return cantidadCarpas; }
        public void setCantidadCarpas(long cantidadCarpas) { this.cantidadCarpas = cantidadCarpas; }
        public long getTiempoMontaje() { return tiempoMontaje; }
        public void setTiempoMontaje(long tiempoMontaje) { this.tiempoMontaje = tiempoMontaje; }
        public Long getFestivalId() { return festivalId; }
        public void setFestivalId(Long festivalId) { this.festivalId = festivalId; }
    }
}