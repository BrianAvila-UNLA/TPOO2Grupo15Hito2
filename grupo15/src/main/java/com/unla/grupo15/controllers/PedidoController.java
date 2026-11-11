package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Pedido;
import com.unla.grupo15.services.IPedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;

    public PedidoController(IPedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("pedidos", pedidoService.getAll());
        return "pedido/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("pedido", new Pedido());
        return "pedido/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("pedido") Pedido pedido) {
        try {
            pedidoService.insertOrUpdate(pedido);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
        return "redirect:/pedidos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") long id) {
        pedidoService.delete(id);
        return "redirect:/pedidos";
    }
}