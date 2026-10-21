package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.services.IFestivalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/")
public class FestivalController {

    private final IFestivalService festivalService;

    public FestivalController(IFestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("festivales", festivalService.getAll());
        return "festival/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("festival", new Festival());
        return "festival/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("festival") Festival festival) {
        festivalService.insertOrUpdate(festival);
        return "redirect:/festivales";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") long id) {
        festivalService.delete(id);
        return "redirect:/festivales";
    }
}
