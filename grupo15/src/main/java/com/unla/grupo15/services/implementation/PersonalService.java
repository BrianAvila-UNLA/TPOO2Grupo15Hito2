package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.entities.AsignacionPersonal;
import com.unla.grupo15.repositories.ICocineroRepository;
import com.unla.grupo15.repositories.IAsignacionPersonalRepository;
import com.unla.grupo15.repositories.IPersonalRepository;
import com.unla.grupo15.services.IPersonalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PersonalService implements IPersonalService {

    private final IPersonalRepository personalRepository;
    private final ICocineroRepository cocineroRepository;
    private final IAsignacionPersonalRepository asignacionRepository;

    public PersonalService(IPersonalRepository personalRepository, ICocineroRepository cocineroRepository,
                           IAsignacionPersonalRepository asignacionRepository) {
        this.personalRepository = personalRepository;
        this.cocineroRepository = cocineroRepository;
        this.asignacionRepository = asignacionRepository;
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
}
