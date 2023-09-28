package com.aarish.flightguide.controllers;

import com.aarish.flightguide.dto.requests.ItineraryRequest;
import com.aarish.flightguide.services.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/itinerary")
@RequiredArgsConstructor
public class ItineraryController {
    private final ItineraryService itineraryService;


    @PostMapping
    public ResponseEntity<String> createTravelItinerary(@RequestBody ItineraryRequest itineraryRequest) {
        String travelItineraryResponse = null;
        try {
            travelItineraryResponse = itineraryService.createTravelItinerary(itineraryRequest);
            return ResponseEntity.ok(travelItineraryResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
