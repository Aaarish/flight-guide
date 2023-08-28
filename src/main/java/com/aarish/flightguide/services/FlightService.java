package com.aarish.flightguide.services;

import com.aarish.flightguide.dto.requests.FlightDetailsRequest;
import com.aarish.flightguide.dto.responses.CustomResponse;


public interface FlightService {

    CustomResponse getFlightDetails(FlightDetailsRequest flightDetailsRequest);

}
