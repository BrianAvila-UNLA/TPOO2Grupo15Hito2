package com.unla.grupo15.services;

import com.unla.grupo15.entities.FoodTruck;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.PuestoDesarmable;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.entities.RolUsuario;
import com.unla.grupo15.repositories.IUnidadVentaRepository;
import com.unla.grupo15.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;

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
        String codigoUnico = registration.codigoUnico() == null
                ? null
                : registration.codigoUnico().trim().toUpperCase(Locale.ROOT);
        if (codigoUnico == null || !codigoUnico.matches(UnidadVenta.FORMATO_CODIGO_UNICO)) {
            throw new IllegalArgumentException(
                    "El código debe tener 10 caracteres: letras, números o guiones.");
        }
        if (existeCodigoUnico(codigoUnico)) {
            throw new IllegalArgumentException("Ya existe una unidad de venta con ese código.");
        }
        if (existeEmailResponsable(registration.email())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese correo.");
        }
        if (existeNombreResponsable(registration.nombreResponsable())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese nombre.");
        }

        String password = generatePassword();
        Usuario responsable = new Usuario(
                registration.nombreResponsable(),
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
        unidad.setCodigoUnico(codigoUnico);
        unidad.setResponsableCargo(responsable);
        unidad.setFestival(registration.festival());
        unidadVentaRepository.save(unidad);

        sendPassword(registration.email(), password);
    }

    public boolean existeCodigoUnico(String codigoUnico) {
        return codigoUnico != null && unidadVentaRepository.existsByCodigoUnico(
                codigoUnico.trim().toUpperCase(Locale.ROOT));
    }

    public boolean existeEmailResponsable(String email) {
        return email != null && usuarioRepository.existsByEmail(email.trim());
    }

    public boolean existeNombreResponsable(String nombre) {
        return nombre != null && usuarioRepository.existsByNombre(nombre.trim());
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

                Hola. Te damos la bienvenida a Epicentro Gourmet. Gracias por su registro!
                Tu usuario de responsable fue creado con éxito. A continuación te compartimos tus datos de acceso.

                Usuario: %s
                Contraseña temporal: %s

                Usá estos datos para iniciar sesión. Luego usted podrá cambiar la contraseña desde su panel.

                Recordá que nadie del equipo le solicitará sus datos personales.

                Saludos,
                El equipo de Epicentro Gourmet
                """.formatted(email, password));
        mailSender.send(message);
    }

    public record UnitRegistration(
            String tipo,
            String nombreComercial,
            String nombreResponsable,
            long superficie,
            String codigoUnico,
            String email,
            String patente,
            boolean conexionElectrica,
            long cantidadCarpas,
            long tiempoMontaje,
            Festival festival) {
    }
}
