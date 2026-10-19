package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Personal;
import com.unla.grupo15.repositories.IPersonalRepository;
import com.unla.grupo15.services.IPersonalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("personalService")
@SuppressWarnings("unused")
public class PersonalService implements IPersonalService {

    private final IPersonalRepository personalRepository;

    public PersonalService(IPersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
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

}
