package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Plato;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.services.IPlatoService;
import com.unla.grupo15.services.IUnidadVentaService;
import com.unla.grupo15.services.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/platos")
public class PlatoController {

    private final IPlatoService platoService;

    private final IUsuarioService usuarioService;

    private final IUnidadVentaService unidadVentaService;

    public PlatoController(IPlatoService platoService, IUsuarioService usuarioService, IUnidadVentaService unidadVentaService) {
        this.platoService = platoService;
        this.usuarioService = usuarioService;
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("platos", platoService.getAll());
        return "plato/index";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

        if (unidadVenta == null) {
            return "redirect:/";
        }

        Plato plato = new Plato();
        plato.setUnidadVenta(unidadVenta);

        model.addAttribute("plato", plato);
        model.addAttribute("unidadVenta", unidadVenta);
        return "plato/registrar-plato";
    }

    @PostMapping("/guardar")
    public String registrarPlato(@Valid @ModelAttribute("plato") Plato plato,
                                 BindingResult result,
                                 Authentication authentication) {
        if (result.hasErrors()) {
            return "plato/registrar-plato";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        UnidadVenta unidadVenta = unidadVentaService.findByResponsableCargo(usuario);

        if (unidadVenta != null) {
            plato.setUnidadVenta(unidadVenta);
            platoService.insertOrUpdate(plato);
        }

        return "redirect:/responsable?exitoPlato";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") long id) {
        platoService.delete(id);
        return "redirect:/platos";
    }


}