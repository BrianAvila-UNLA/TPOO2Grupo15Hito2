package com.unla.grupo15.services.implementation;

import com.unla.grupo15.entities.Festival;
import com.unla.grupo15.repositories.IFestivalRepository;
import com.unla.grupo15.services.IFestivalService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("festivalService")
@SuppressWarnings("unused")
public class FestivalService implements IFestivalService {

    private final IFestivalRepository festivalRepository;
    private final ModelMapper modelMapper;

    public FestivalService(IFestivalRepository festivalRepository, ModelMapper modelMapper) {
        this.festivalRepository = festivalRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Festival> getAll() {
        return festivalRepository.findAll();
    }

    @Override
    public Optional<Festival> findById(long id) {
        return festivalRepository.findById(id);
    }

    @Override
    public Festival insertOrUpdate(Festival festival) {
        return festivalRepository.save(festival);
    }

    @Override
    public void delete(long id) {
        festivalRepository.deleteById(id);
    }

}
