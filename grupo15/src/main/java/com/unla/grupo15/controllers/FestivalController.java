package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.services.IFestivalService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/festivales")
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
    public String guardar(@Valid @ModelAttribute("festival") Festival festival,
                          BindingResult result,
                          HttpServletResponse response) throws IOException {
        if (result.hasErrors()) {
            String mensaje = result.getAllErrors().get(0).getDefaultMessage();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, mensaje);
            return null;
        }
        festivalService.insertOrUpdate(festival);
        return "redirect:/festivales";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") long id) {
        festivalService.delete(id);
        return "redirect:/festivales";
    }
}
