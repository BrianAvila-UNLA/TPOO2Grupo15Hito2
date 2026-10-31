package com.unla.grupo15.controllers;

import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.dto.ReporteMayoresCanonDTO;
import com.unla.grupo15.services.IFestivalService;
import com.unla.grupo15.services.IPedidoService;
import com.unla.grupo15.services.IUnidadVentaService;
import com.unla.grupo15.services.IUsuarioService;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final IUnidadVentaService unidadVentaService;
    private final IPedidoService pedidoService;
    private final IFestivalService festivalService;
    private final IUsuarioService usuarioService;

    public AdminController(
            IUnidadVentaService unidadVentaService,
            IPedidoService pedidoService,
            IFestivalService festivalService,
            IUsuarioService usuarioService) {
        this.unidadVentaService = unidadVentaService;
        this.pedidoService = pedidoService;
        this.festivalService = festivalService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/inicio")
    public String inicio(
            @RequestParam(required = false) Long festivalId,
            @RequestParam(defaultValue = "3") int limite,
            Model model,
            Authentication authentication) {
        List<Festival> festivales = festivalService.getAll();
        Festival festivalSeleccionado = seleccionarFestival(festivales, festivalId);
        int limiteSeguro = Math.max(1, limite);

        List<UnidadVenta> unidades = unidadVentaService.getAll();
        List<RankingUnidad> ranking = unidades.stream()
                .map(unidad -> new RankingUnidad(
                        unidad,
                        recaudacionDe(unidad)))
                .sorted(Comparator.comparing(RankingUnidad::recaudacion).reversed()
                        .thenComparing(rankingUnidad -> rankingUnidad.unidad().getNombreComercial()))
                .toList();

        model.addAttribute("unidades", ranking.stream()
                .map(RankingUnidad::unidad)
                .toList());
        model.addAttribute("rankingUnidades", ranking);
        model.addAttribute("festivales", festivales);
        model.addAttribute("festivalSeleccionado", festivalSeleccionado);
        model.addAttribute("limite", limiteSeguro);
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario usuario = usuarioService.findByEmail(authentication.getName()).orElse(null);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
            }
        }
        return "unidadVenta/index";
    }

    @GetMapping("/reportes/mayores-canon/pdf")
    public void descargarMayoresCanonPdf(
            @RequestParam long festivalId,
            @RequestParam int limite,
            HttpServletResponse response) throws IOException {
        if (limite < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "La cantidad de unidades debe ser mayor que cero.");
            return;
        }

        Optional<Festival> festivalOpt = festivalService.findById(festivalId);
        if (festivalOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Festival no encontrado.");
            return;
        }

        Festival festival = festivalOpt.get();
        if (festival.getCosto() == null) {
            response.sendError(HttpServletResponse.SC_CONFLICT,
                    "El festival no tiene costos configurados para calcular el canon.");
            return;
        }

        List<UnidadVenta> unidadesDelFestival = unidadVentaService.getAll().stream()
                .filter(unidad -> unidad.getFestival() != null
                        && unidad.getFestival().getIdFestival() == festival.getIdFestival())
                .toList();

        if (limite > unidadesDelFestival.size()) {
            response.sendRedirect("/admin/inicio?festivalId=" + festivalId
                    + "&limite=" + limite
                    + "&error=cantidad-excede"
                    + "&disponibles=" + unidadesDelFestival.size());
            return;
        }

        List<ReporteMayoresCanonDTO> mayoresCanon = unidadesDelFestival.stream()
                .map(this::crearReporteCanon)
                .sorted(Comparator.comparing(ReporteMayoresCanonDTO::getCanon).reversed()
                        .thenComparing(ReporteMayoresCanonDTO::getNombreComercial))
                .limit(limite)
                .toList();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=unidades_mayor_canon_" + festival.getIdFestival() + ".pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("--- EPICENTRO GOURMET ---"));
        document.add(new Paragraph("Unidades con mayor canon"));
        document.add(new Paragraph("Festival: " + festival.getNombre()));
        document.add(new Paragraph("Cantidad solicitada: " + limite));
        document.add(new Paragraph("------------------------------------------"));

        if (mayoresCanon.isEmpty()) {
            document.add(new Paragraph("No hay unidades asignadas a este festival."));
        } else {
            for (int i = 0; i < mayoresCanon.size(); i++) {
                ReporteMayoresCanonDTO reporte = mayoresCanon.get(i);
                document.add(new Paragraph((i + 1) + ". " + reporte.getNombreComercial()));
                document.add(new Paragraph("Código: " + reporte.getCodigoUnico()
                        + " | Tipo: " + reporte.getTipoUnidad()
                        + " | Canon: $" + reporte.getCanon()));
            }
        }
        String fechaHoraSolicitud = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        document.add(new Paragraph("------------------------------------------"));
        document.add(new Paragraph("Fecha y hora de solicitud: " + fechaHoraSolicitud));
        document.close();
    }

    private Festival seleccionarFestival(List<Festival> festivales, Long festivalId) {
        if (festivalId != null) {
            return festivales.stream()
                    .filter(festival -> festival.getIdFestival() == festivalId)
                    .findFirst()
                    .orElse(null);
        }
        return festivales.isEmpty() ? null : festivales.get(0);
    }

    private ReporteMayoresCanonDTO crearReporteCanon(UnidadVenta unidad) {
        return new ReporteMayoresCanonDTO(
                unidad.getNombreComercial(),
                unidad.getCodigoUnico(),
                unidad.getClass().getSimpleName().equals("FoodTruck")
                        ? "Food Truck"
                        : "Puesto desarmable",
                unidad.calcularCanon());
    }

    private BigDecimal recaudacionDe(UnidadVenta unidad) {
        BigDecimal recaudacion = pedidoService.sumarRecaudacionPorUnidad(unidad);
        return recaudacion == null ? BigDecimal.ZERO : recaudacion;
    }

    public record RankingUnidad(UnidadVenta unidad, BigDecimal recaudacion) {
    }
}
