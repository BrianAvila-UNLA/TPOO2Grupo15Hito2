package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Cajero;
import com.unla.grupo15.entities.Cocinero;
import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.services.IPersonalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/personal")
public class PersonalController {

    private final IPersonalService personalService;

    public PersonalController(IPersonalService personalService) {
        this.personalService = personalService;
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
}