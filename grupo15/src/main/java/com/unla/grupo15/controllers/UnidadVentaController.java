package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.FoodTruck;
import com.unla.grupo15.entities.PuestoDesarmable;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.services.IUnidadVentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/unidades")
public class UnidadVentaController {

    private final IUnidadVentaService unidadVentaService;

    public UnidadVentaController(IUnidadVentaService unidadVentaService) {
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("unidades", unidadVentaService.getAll());
        return "unidadVenta/index";
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
}