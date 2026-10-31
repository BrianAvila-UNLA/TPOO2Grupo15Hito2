package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.AsignacionPersonal;
import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.entities.RolUsuario;
import com.unla.grupo15.entities.UnidadVenta;
import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.repositories.IAsignacionPersonalRepository;
import com.unla.grupo15.repositories.ICocineroRepository;
import com.unla.grupo15.repositories.IPersonalRepository;
import com.unla.grupo15.repositories.IUsuarioRepository;
import com.unla.grupo15.services.IPersonalService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

@Service
public class PersonalService implements IPersonalService {

    private final IPersonalRepository personalRepository;
    private final ICocineroRepository cocineroRepository;
    private final IAsignacionPersonalRepository asignacionRepository;
    private final IUsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final LiquidacionService liquidacionService;
    private final String senderEmail;

    public PersonalService(IPersonalRepository personalRepository, 
                           ICocineroRepository cocineroRepository,
                           IAsignacionPersonalRepository asignacionRepository,
                           IUsuarioRepository usuarioRepository, 
                           JavaMailSender mailSender, 
                           PasswordEncoder passwordEncoder,
                           LiquidacionService liquidacionService,
                           @Value("${spring.mail.username}") String senderEmail) {
        this.personalRepository = personalRepository;
        this.cocineroRepository = cocineroRepository;
        this.asignacionRepository = asignacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.liquidacionService = liquidacionService;
        this.senderEmail = senderEmail;
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
    @Transactional
    public Personal insertOrUpdate(Personal personal) {
        Personal saved = personalRepository.save(personal);
        Optional<AsignacionPersonal> asignacionAbierta = asignacionRepository
                .findFirstByPersonalAndFechaFinIsNullOrderByFechaInicioDesc(saved);

        if (saved.getUnidadVenta() == null || saved.getUnidadVenta().getFestival() == null) {
            asignacionAbierta.ifPresent(this::cerrarAsignacion);
            return saved;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate inicioFestival = saved.getUnidadVenta().getFestival().getFechaInicio();
        LocalDate fechaInicioAsignacion = inicioFestival != null && inicioFestival.isBefore(hoy)
                ? inicioFestival
                : hoy;
        LocalDate finFestival = saved.getUnidadVenta().getFestival().getFechaFin();
        LocalDate fechaFinAsignacion = finFestival != null && finFestival.isBefore(hoy)
                ? finFestival
                : null;

        if (asignacionAbierta.isPresent()) {
            AsignacionPersonal actual = asignacionAbierta.get();
            if (actual.getUnidadVenta().getIdUnidadVenta() == saved.getUnidadVenta().getIdUnidadVenta()
                    && actual.getFestival().getIdFestival() == saved.getUnidadVenta().getFestival().getIdFestival()) {
                if (fechaFinAsignacion != null && actual.getFechaFin() == null) {
                    actual.setFechaFin(fechaFinAsignacion);
                    asignacionRepository.save(actual);
                }
                return saved;
            }
            cerrarAsignacion(actual);
        }

        AsignacionPersonal nuevaAsignacion = new AsignacionPersonal(
                saved, saved.getUnidadVenta(), saved.getUnidadVenta().getFestival(), fechaInicioAsignacion);
        nuevaAsignacion.setFechaFin(fechaFinAsignacion);
        asignacionRepository.save(nuevaAsignacion);
        return saved;
    }

    @Override
    public void delete(long id) {
        personalRepository.deleteById(id);
    }

    @Override
    public Optional<Personal> findByEmail(String email) {
        return personalRepository.findByEmail(email);
    }

    @Override
    public Optional<Personal> findByCredencialesEmail(String email) {
        return personalRepository.findByCredencialesEmail(email);
    }

    @Override
    public boolean existsByDni(int dni) {
        return personalRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByDniExcluding(int dni, long idPersonal) {
        return personalRepository.existsByDniAndIdPersonalNot(dni, idPersonal);
    }

    @Override
    public boolean existsByEmail(String email) {
        return personalRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailExcluding(String email, long idPersonal) {
        return personalRepository.existsByEmailAndIdPersonalNot(email, idPersonal);
    }

    @Override
    @Transactional
    public Personal actualizarEmpleado(Personal empleado) {
        if (empleado.getCredenciales() != null) {
            empleado.getCredenciales().setEmail(empleado.getEmail());
            empleado.getCredenciales().setNombre(nombreUsuario(empleado));
            usuarioRepository.save(empleado.getCredenciales());
        }
        return insertOrUpdate(empleado);
    }

    @Transactional
    public List<AsignacionPersonal> obtenerHistorialAsignaciones(Personal personal) {
        List<AsignacionPersonal> historial = asignacionRepository.findByPersonalOrderByFechaInicioDesc(personal);
        if (historial.isEmpty() && personal.getUnidadVenta() != null
                && personal.getUnidadVenta().getFestival() != null) {
            var festival = personal.getUnidadVenta().getFestival();
            LocalDate inicioFestival = festival.getFechaInicio();
            LocalDate inicio = inicioFestival == null ? LocalDate.now() : inicioFestival;
            AsignacionPersonal asignacion = new AsignacionPersonal(
                    personal,
                    personal.getUnidadVenta(),
                    festival,
                    inicio);
            if (festival.getFechaFin() != null && festival.getFechaFin().isBefore(LocalDate.now())) {
                asignacion.setFechaFin(festival.getFechaFin());
            }
            asignacionRepository.save(asignacion);
            historial = asignacionRepository.findByPersonalOrderByFechaInicioDesc(personal);
        }

        LocalDate hoy = LocalDate.now();
        for (AsignacionPersonal asignacion : historial) {
            LocalDate fechaFinFestival = asignacion.getFestival().getFechaFin();
            if (asignacion.getFechaFin() == null && fechaFinFestival != null && fechaFinFestival.isBefore(hoy)) {
                asignacion.setFechaFin(fechaFinFestival);
                asignacionRepository.save(asignacion);
            }
        }
        return historial;
    }

    @Override
    public Optional<AsignacionPersonal> findAsignacion(long idAsignacion, Personal personal) {
        return asignacionRepository.findByIdAsignacionAndPersonal(idAsignacion, personal);
    }

    @Override
    public boolean esCocinero(String email) {
        Optional<Personal> personalOpt = this.findByCredencialesEmail(email);
        if (personalOpt.isPresent()) {
            return cocineroRepository.findById(personalOpt.get().getIdPersonal()).isPresent();
        }

        return false;
    }

    @Override
    public BigDecimal calcularSueldos(UnidadVenta unidadVenta, Festival festival) {
        BigDecimal total = BigDecimal.ZERO;
        for (Personal personal : unidadVenta.getPersonal()) {
            if (!personal.isEliminado() && personal.getCredenciales() != null
                    && personal.getCredenciales().isActivo()) {
                total = total.add(liquidacionService.calcularSueldo(personal, festival));
            }
        }
        return total;
    }

    @Override
    public BigDecimal calcularSueldos(UnidadVenta unidadVenta, Festival festival,
                                      LocalDate desde, LocalDate hasta) {
        Festival periodo = new Festival(festival.getNombre(), festival.getTemporada(),
                desde, hasta, festival.getCosto());
        BigDecimal total = BigDecimal.ZERO;
        for (Personal personal : unidadVenta.getPersonal()) {
            if (!personal.isEliminado() && personal.getCredenciales() != null
                    && personal.getCredenciales().isActivo()) {
                total = total.add(liquidacionService.calcularSueldo(personal, periodo));
            }
        }
        return total;
    }

    private void cerrarAsignacion(AsignacionPersonal asignacion) {
        LocalDate fin = LocalDate.now();
        LocalDate fechaFinFestival = asignacion.getFestival().getFechaFin();
        if (fechaFinFestival != null && fechaFinFestival.isBefore(fin)) {
            fin = fechaFinFestival;
        }
        if (fin.isBefore(asignacion.getFechaInicio())) {
            fin = asignacion.getFechaInicio();
        }
        asignacion.setFechaFin(fin);
        asignacionRepository.save(asignacion);
    }

    @Override
    @Transactional(noRollbackFor = MailException.class)
    public void registrarEmpleado(Personal empleado, UnidadVenta unidadVenta) {
        String passwordAleatoria = UUID.randomUUID().toString().substring(0, 8);

        Usuario credenciales = new Usuario();

        credenciales.setEmail(empleado.getEmail());
        credenciales.setNombre(nombreUsuario(empleado));
        credenciales.setContrasenia(passwordEncoder.encode(passwordAleatoria));
        credenciales.setRol(RolUsuario.EMPLEADO);
        credenciales.setActivo(true);

        usuarioRepository.save(credenciales);

        empleado.setCredenciales(credenciales);
        empleado.setUnidadVenta(unidadVenta);
        empleado.setEliminado(false);

        personalRepository.save(empleado);

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(senderEmail);
        mensaje.setTo(empleado.getEmail());
        mensaje.setSubject("Tus credenciales de acceso - Epicentro Gourmet");
        mensaje.setText("Hola " + empleado.getNombre() + ",\n\nSe ha creado tu cuenta como empleado.\n" +
                "Tus datos de acceso son:\n" +
                "Usuario (Email): " + empleado.getEmail() + "\n" +
                "Contraseña temporal: " + passwordAleatoria + "\n\n" +
                "Por favor, inicia sesión en la plataforma.");

        mailSender.send(mensaje);
    }

    private String nombreUsuario(Personal empleado) {
        return empleado.getNombre();
    }

    @Override
    public void bajaLogica(Long id) {
        Personal personal = personalRepository.findById(id).orElse(null);
        if (personal != null) {
            personal.setEliminado(true);
            if (personal.getCredenciales() != null) {
                personal.getCredenciales().setActivo(false);
                usuarioRepository.save(personal.getCredenciales());
            }
            personalRepository.save(personal);
        }
    }

    @Override
    public void inactivar(Long id) {
        Personal personal = personalRepository.findById(id).orElse(null);
        if (personal != null && personal.getCredenciales() != null) {
            personal.getCredenciales().setActivo(false);
            usuarioRepository.save(personal.getCredenciales());
        }
    }

    @Override
    public void darDeAlta(Long id) {
        Personal personal = personalRepository.findById(id).orElse(null);
        if (personal != null && !personal.isEliminado()) {
            if (personal.getCredenciales() != null) {
                personal.getCredenciales().setActivo(true);
                usuarioRepository.save(personal.getCredenciales());
            }
            personalRepository.save(personal);
        }
    }
}
