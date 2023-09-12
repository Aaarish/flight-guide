package com.aarish.flightguide.repositories;

import com.aarish.flightguide.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepo extends JpaRepository<City, String> {

    Optional<City> findByCityName(String cityName);

}
