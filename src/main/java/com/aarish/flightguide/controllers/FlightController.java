package com.aarish.flightguide.controllers;

import com.aarish.flightguide.dto.requests.FlightDetailsRequest;
import com.aarish.flightguide.dto.responses.CustomResponse;
import com.aarish.flightguide.services.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<CustomResponse> getFlightDetails(@RequestBody FlightDetailsRequest flightDetailsRequest) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(flightService.getAllFlightDetails(flightDetailsRequest));
    }

    @PostMapping("/cheapest")
    public ResponseEntity<CustomResponse> getTenCheapestFlightDetails(@RequestBody FlightDetailsRequest flightDetailsRequest) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(flightService.getTenCheapestFlightDetails(flightDetailsRequest));
    }

}
