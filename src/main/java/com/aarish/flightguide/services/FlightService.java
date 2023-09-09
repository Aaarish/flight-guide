package com.aarish.flightguide.services;

import com.aarish.flightguide.dto.requests.FlightDetailsRequest;
import com.aarish.flightguide.dto.responses.CustomResponse;


public interface FlightService {

    CustomResponse getAllFlightDetails(FlightDetailsRequest flightDetailsRequest);
    CustomResponse getTenCheapestFlightDetails(FlightDetailsRequest flightDetailsRequest);

}
