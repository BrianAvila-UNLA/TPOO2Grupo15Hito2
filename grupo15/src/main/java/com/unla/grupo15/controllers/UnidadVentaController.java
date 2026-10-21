package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.FoodTruck;
import com.unla.grupo15.entities.PuestoDesarmable;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.services.IUnidadVentaService;
import com.unla.grupo15.services.UnidadVentaRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/unidades")
public class UnidadVentaController {

    private final IUnidadVentaService unidadVentaService;
    private final UnidadVentaRegistrationService registrationService;

    public UnidadVentaController(
            IUnidadVentaService unidadVentaService,
            UnidadVentaRegistrationService registrationService) {
        this.unidadVentaService = unidadVentaService;
        this.registrationService = registrationService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("unidades", unidadVentaService.getAll());
        return "unidadVenta/index";
    }

    @GetMapping("/registrar")
    public String registrar(Model model) {
        model.addAttribute("registro", new RegistroUnidadForm());
        return "unidadVenta/registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute("registro") RegistroUnidadForm form, Model model) {
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
                    form.tiempoMontaje));
            return "redirect:/unidades/registrar?enviado";
        } catch (IllegalArgumentException | org.springframework.mail.MailException exception) {
            model.addAttribute("error", exception.getMessage());
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
        private String nombreComercial;
        private long superficie;
        private String codigoUnico;
        private String email;
        private String patente;
        private boolean conexionElectrica;
        private long cantidadCarpas;
        private long tiempoMontaje;

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
    }
}