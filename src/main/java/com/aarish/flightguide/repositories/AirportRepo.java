package com.aarish.flightguide.repositories;

import com.aarish.flightguide.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepo extends JpaRepository<Airport, String> {

    Optional<Airport> findByAirportName(String airportName);
    Optional<Airport> findByCityCode(String cityCode);

}
