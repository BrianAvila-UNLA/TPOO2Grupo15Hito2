package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Plato;
import com.unla.grupo15.services.IPlatoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/platos")
public class PlatoController {

    private final IPlatoService platoService;

    public PlatoController(IPlatoService platoService) {
        this.platoService = platoService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("platos", platoService.getAll());
        return "plato/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("plato", new Plato());
        return "plato/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("plato") Plato plato) {
        platoService.insertOrUpdate(plato);
        return "redirect:/platos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") long id) {
        platoService.delete(id);
        return "redirect:/platos";
    }
}