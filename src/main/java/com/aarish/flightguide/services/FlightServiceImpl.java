package com.aarish.flightguide.services;

import com.aarish.flightguide.dto.requests.FlightDetailsRequest;
import com.aarish.flightguide.dto.responses.CustomResponse;
import com.aarish.flightguide.dto.responses.FlightDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private static final String REMOTE_API_BASE_URL = "https://api.flightapi.io/onewaytrip/64eb9b44e7606d63a78b8147";

    //"https://api.flightapi.io/onewaytrip/api_key/departure_airport_code/arrival_airport_code/departure_date/number_of_adults/number_of_childrens/number_of_infants/cabin_class/currency";

    private final RestTemplate restTemplate;

    @Override
    public CustomResponse getFlightDetails(FlightDetailsRequest flightDetailsRequest) {
        List<FlightDetail> flightDetailList = new ArrayList<>();

        String url = REMOTE_API_BASE_URL + "/" + flightDetailsRequest.getSource() + "/" + flightDetailsRequest.getDestination() + "/2023-08-29/" + 1 + "/" + 0 + "/" + 0 + "/" + flightDetailsRequest.getCabinClass() + "/USD";
        Map<String, Object> response = null;

        //remote api call
        try {
            log.info("connecting to the remote api !!!");
            response = restTemplate.getForObject(url, HashMap.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to connect to remote api !");
        }

        List<Map<String, Object>> legs = (List<Map<String, Object>>) response.get("legs");
        List<Map<String, Object>> fares = (List<Map<String, Object>>) response.get("fares");

//        Map<String, String>[] airlines = (Map<String, String>[]) response.get("airlines");
//        Map<String, String>[] cities = (Map<String, String>[]) response.get("cities");
//        Map<String, String>[] airports = (Map<String, String>[]) response.get("airports");
//        Map<String, String>[] providers = (Map<String, String>[]) response.get("providers");
//        Map<String, String>[] countries = (Map<String, String>[]) response.get("countries");
//        Map<String, Object>[] trips = (Map<String, Object>[]) response.get("trips");
//        Map<String, Object> filters = (Map<String, Object>) response.get("filters");
//        Map<String, Object>[] routeSponsors = (Map<String, Object>[]) response.get("routeSponsors");

        log.info("Legs : {}", legs.get(0));

        for (int i=0; i<3; i++) {
            Map<String, Integer> price = (Map<String, Integer>) fares.get(i).get("price");
            log.info("Total Price : {}", price.get("totalAmount"));

            FlightDetail flightDetail = FlightDetail.builder()
                    .source((String) legs.get(i).get("departureAirportCode"))
                    .destination((String) legs.get(i).get("arrivalAirportCode"))
                    .flightDurationInHours((String) legs.get(i).get("duration"))
                    .fare(price.get("totalAmount"))
                    .build();

            flightDetailList.add(flightDetail);
        }


        return CustomResponse.builder()
                .flightDetails(flightDetailList.toArray(new FlightDetail[flightDetailList.size()]))
                .build();
    }

}
