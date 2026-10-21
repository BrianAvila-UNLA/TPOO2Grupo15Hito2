package com.unla.grupo15.services;

import com.unla.grupo15.entities.FoodTruck;
import com.unla.grupo15.entities.PuestoDesarmable;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.entities.RolUsuario;
import com.unla.grupo15.repositories.IUnidadVentaRepository;
import com.unla.grupo15.repositories.IUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UnidadVentaRegistrationService {

    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private final IUnidadVentaRepository unidadVentaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final String senderEmail;
    private final SecureRandom secureRandom = new SecureRandom();

    public UnidadVentaRegistrationService(
            IUnidadVentaRepository unidadVentaRepository,
            IUsuarioRepository usuarioRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder,
            @Value("${spring.mail.username}") String senderEmail) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.senderEmail = senderEmail;
    }

    @Transactional
    public void register(UnitRegistration registration) {
        String password = generatePassword();
        Usuario responsable = new Usuario(
                registration.email(),
                passwordEncoder.encode(password),
                RolUsuario.RESPONSABLE,
                true);
        responsable.setEmail(registration.email());
        usuarioRepository.save(responsable);

        UnidadVenta unidad = switch (registration.tipo()) {
            case "FOODTRUCK" -> {
                FoodTruck foodTruck = new FoodTruck();
                foodTruck.setPatente(registration.patente());
                foodTruck.setConexionElectrica(registration.conexionElectrica());
                yield foodTruck;
            }
            case "PUESTO" -> {
                PuestoDesarmable puesto = new PuestoDesarmable();
                puesto.setCantidadCarpas(registration.cantidadCarpas());
                puesto.setTiempoMontaje(registration.tiempoMontaje());
                yield puesto;
            }
            default -> throw new IllegalArgumentException("Tipo de unidad de venta inválido");
        };

        unidad.setNombreComercial(registration.nombreComercial());
        unidad.setSuperficie(registration.superficie());
        unidad.setCodigoUnico(registration.codigoUnico());
        unidad.setResponsableCargo(responsable);
        unidadVentaRepository.save(unidad);

        sendPassword(registration.email(), password);
    }

    private String generatePassword() {
        StringBuilder password = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            password.append(ALPHANUMERIC.charAt(secureRandom.nextInt(ALPHANUMERIC.length())));
        }
        return password.toString();
    }

    private void sendPassword(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject("Alta de responsable - Epicentro Gourmet");
        message.setText("""
                Tu usuario de responsable fue creado en Epicentro Gourmet.

                Usuario: %s
                Contraseña temporal: %s

                Usá estos datos para iniciar sesión.
                """.formatted(email, password));
        mailSender.send(message);
    }

    public record UnitRegistration(
            String tipo,
            String nombreComercial,
            long superficie,
            String codigoUnico,
            String email,
            String patente,
            boolean conexionElectrica,
            long cantidadCarpas,
            long tiempoMontaje) {
    }
}
