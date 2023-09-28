package com.aarish.flightguide.services;

import com.aarish.flightguide.dto.requests.ItineraryRequest;
import org.json.JSONObject;

public interface ItineraryService {

    String createTravelItinerary(ItineraryRequest itineraryRequest);

}
