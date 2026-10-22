package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.services.IFestivalService;
import com.unla.grupo15.services.IPlatoService;
import com.unla.grupo15.services.IUnidadVentaService;
import com.unla.grupo15.services.IUsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    private final IFestivalService festivalService;

    private final IUsuarioService usuarioService;

    private final IUnidadVentaService unidadVentaService;

    public HomeController(IFestivalService festivalService, IUsuarioService usuarioService, IUnidadVentaService unidadVentaService) {
        this.festivalService = festivalService;
        this.usuarioService = usuarioService;
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(Model model, Authentication authentication) {
        model.addAttribute("festivales", festivalService.getAll());

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Usuario usuario = usuarioService.findByEmail(email).orElse(null);

            if (usuario != null) {
                model.addAttribute("usuario", usuario);

                UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);
                if (unidadVenta != null) {
                    return "redirect:/responsable";
                }
            }
        }
        return "index";
    }

}