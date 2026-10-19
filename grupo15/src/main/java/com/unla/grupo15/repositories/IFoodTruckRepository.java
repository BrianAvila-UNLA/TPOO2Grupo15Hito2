package com.unla.grupo15.repositories;

import com.unla.grupo15.entities.FoodTruck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("foodTruckRepository")
public interface IFoodTruckRepository extends JpaRepository<FoodTruck, Long> {

    public abstract Optional<FoodTruck> findById(long id);

}
