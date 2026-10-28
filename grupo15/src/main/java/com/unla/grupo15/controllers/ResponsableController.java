package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Plato;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.services.IPlatoService;
import com.unla.grupo15.services.IUnidadVentaService;
import com.unla.grupo15.services.IUsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/responsable")
public class ResponsableController {

    private final IUsuarioService usuarioService;
    private final IUnidadVentaService unidadVentaService;
    private final IPlatoService platoService;

    public ResponsableController(IUsuarioService usuarioService, IUnidadVentaService unidadVentaService, IPlatoService platoService) {
        this.usuarioService = usuarioService;
        this.unidadVentaService = unidadVentaService;
        this.platoService = platoService;
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

                BigDecimal canon = unidadVenta.calcularCanon();
                model.addAttribute("canon", canon);
            }
        }

        return "usuario/responsable";
    }
}
