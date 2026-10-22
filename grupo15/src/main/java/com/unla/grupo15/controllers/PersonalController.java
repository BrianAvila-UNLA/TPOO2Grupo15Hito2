package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Cajero;
import com.unla.grupo15.entities.Cocinero;
import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.services.IPersonalService;
import com.unla.grupo15.services.implementation.LiquidacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/personal")
public class PersonalController {

    private final IPersonalService personalService;
    private final LiquidacionService liquidacionService;

    public PersonalController(IPersonalService personalService, LiquidacionService liquidacionService) {
        this.personalService = personalService;
        this.liquidacionService = liquidacionService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("personalList", personalService.getAll());
        return "personal/index";
    }

    @GetMapping("/nuevo/cajero")
    public String nuevoCajero(Model model) {
        model.addAttribute("personal", new Cajero());
        return "personal/nuevo-cajero";
    }

    @GetMapping("/nuevo/cocinero")
    public String nuevoCocinero(Model model) {
        model.addAttribute("personal", new Cocinero());
        return "personal/nuevo-cocinero";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("personal") Personal personal) {
        personalService.insertOrUpdate(personal);
        return "redirect:/personal";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") long id) {
        personalService.delete(id);
        return "redirect:/personal";
    }

    @GetMapping("/liquidacion")
    public String verLiquidacion(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String emailUsuario = principal.getName();
        Optional<Personal> personalOpt = personalService.findByEmail(emailUsuario);

        if (personalOpt.isPresent()) {
            Personal personal = personalOpt.get();

            int diasFestivalEjemplo = 20;

            double totalNeto = liquidacionService.calcularSueldo(personal, diasFestivalEjemplo);

            model.addAttribute("personal", personal);
            model.addAttribute("totalNeto", totalNeto);
            model.addAttribute("diasFestival", diasFestivalEjemplo);
        } else {
            model.addAttribute("errorLiquidacion", "No se encontró información de personal asociada a este usuario.");
        }

        return "personal/liquidacion";
    }
}