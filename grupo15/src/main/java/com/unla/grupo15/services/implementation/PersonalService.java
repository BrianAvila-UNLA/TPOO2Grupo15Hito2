package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.RolUsuario;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.repositories.IPersonalRepository;
import com.unla.grupo15.repositories.IUsuarioRepository;
import com.unla.grupo15.services.IPersonalService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("personalService")
@SuppressWarnings("unused")
public class PersonalService implements IPersonalService {

    private final IPersonalRepository personalRepository;
    private final IUsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public PersonalService(IPersonalRepository personalRepository, IUsuarioRepository usuarioRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.personalRepository = personalRepository;
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Personal> getAll() {
        return personalRepository.findAll();
    }

    @Override
    public Optional<Personal> findById(long id) {
        return personalRepository.findById(id);
    }

    @Override
    public Personal insertOrUpdate(Personal personal) {
        return personalRepository.save(personal);
    }

    @Override
    public void delete(long id) {
        personalRepository.deleteById(id);
    }

    @Override
    public Optional<Personal> findByEmail(String email) {
        return personalRepository.findByCredencialesEmail(email);
    }

    @Override
    public boolean existsByDni(int dni) {
        return personalRepository.existsByDni(dni);
    }

    @Override
    public void registrarEmpleado(Personal empleado, UnidadVenta unidadVenta) {
        String passwordAleatoria = UUID.randomUUID().toString().substring(0, 8);

        Usuario credenciales = new Usuario();
        credenciales.setEmail(empleado.getEmail());
        credenciales.setNombre(empleado.getEmail());
        credenciales.setContrasenia(passwordEncoder.encode(passwordAleatoria));
        credenciales.setRol(RolUsuario.EMPLEADO);
        credenciales.setActivo(true);

        usuarioRepository.save(credenciales);

        empleado.setCredenciales(credenciales);
        empleado.setUnidadVenta(unidadVenta);

        personalRepository.save(empleado);

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(empleado.getEmail());
        mensaje.setSubject("Tus credenciales de acceso - Epicentro Gourmet");
        mensaje.setText("Hola " + empleado.getNombre() + ",\n\nSe ha creado tu cuenta como empleado.\n" +
                "Tus datos de acceso son:\n" +
                "Usuario (Email): " + empleado.getEmail() + "\n" +
                "Contraseña temporal: " + passwordAleatoria + "\n\n" +
                "Por favor, inicia sesión en la plataforma.");

        try {
            mailSender.send(mensaje);
        } catch (Exception e) {
            // Manejo de error de correo
        }
    }

    public void bajaLogica(Long id) {
        Personal personal = personalRepository.findById(id).orElse(null);
        if (personal != null) {
            if (personal.getCredenciales() != null) {
                personal.getCredenciales().setActivo(false);
                usuarioRepository.save(personal.getCredenciales());
            }
            personalRepository.save(personal);
        }
    }

    public void darDeAlta(Long id) {
        Personal personal = personalRepository.findById(id).orElse(null);
        if (personal != null) {
            if (personal.getCredenciales() != null) {
                personal.getCredenciales().setActivo(true);
                usuarioRepository.save(personal.getCredenciales());
            }
            personalRepository.save(personal);
        }
    }

}
