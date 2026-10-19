package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("festivalRepository")
public interface IFestivalRepository extends JpaRepository<Festival, Long> {

    public abstract Optional<Festival> findById(long id);

}
