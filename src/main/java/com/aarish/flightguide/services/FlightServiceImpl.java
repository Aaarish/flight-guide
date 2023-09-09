package com.aarish.flightguide.services;

import com.aarish.flightguide.dto.requests.FlightDetailsRequest;
import com.aarish.flightguide.dto.responses.CustomResponse;
import com.aarish.flightguide.dto.responses.FlightDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {
    private static final String REMOTE_API_BASE_URL = "https://api.flightapi.io/onewaytrip/61931a3db3a068175889689a";

    private final RestTemplate restTemplate;

    @Override
    public CustomResponse getAllFlightDetails(FlightDetailsRequest flightDetailsRequest) {
        String url = REMOTE_API_BASE_URL + "/" + flightDetailsRequest.getSource() + "/" + flightDetailsRequest.getDestination() + "/2023-09-10/" + 1 + "/" + 0 + "/" + 0 + "/" + flightDetailsRequest.getCabinClass() + "/USD";
        Map<String, Object> response = null;

        try {
            log.info("connecting to the remote api !!!");
            response = restTemplate.getForObject(url, HashMap.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to connect to remote api : " + e);
        }

        List<FlightDetail> flightDetailList = processData2(flightDetailsRequest, response);
        log.info("flights list size: {}", flightDetailList.size());

        return CustomResponse.builder()
                .flightDetails(flightDetailList.toArray(new FlightDetail[flightDetailList.size()]))
                .build();
    }

    @Override
    public CustomResponse getTenCheapestFlightDetails(FlightDetailsRequest flightDetailsRequest) {
        FlightDetail[] flightDetails = getAllFlightDetails(flightDetailsRequest).getFlightDetails();
        Arrays.sort(flightDetails);

        FlightDetail[] finalResponseArray = new FlightDetail[10];
        for (int i=0; i<10; i++) {
            finalResponseArray[i] = flightDetails[i];
        }

        return CustomResponse.builder()
                .flightDetails(finalResponseArray)
                .build();
    }

    private List<FlightDetail> processData(FlightDetailsRequest request, Map<String, Object> response) {
        List<FlightDetail> flightDetailList = new ArrayList<>();


        List<Map<String, Object>> trips = (List<Map<String, Object>>) response.get("trips");
        List<Map<String, Object>> fares = (List<Map<String, Object>>) response.get("fares");

        int sampleSize = Math.min(trips.size(), fares.size());
        log.info("Loop will run {} times", sampleSize);

        for(int i=0; i<sampleSize; i++) {
            FlightDetail flightDetail = new FlightDetail();

            Map<String, Object> tripsMap = trips.get(i);
            Map<String, Object> faresMap = fares.get(i);

            flightDetail.setSource(request.getSource());
            flightDetail.setDestination(request.getDestination());

            if(tripsMap.get("id").equals(faresMap.get("tripId"))) {
                Map<String, Integer> prices = (Map<String, Integer>) faresMap.get("price");
                log.info("Total Amount : {}", prices.get("totalAmount"));
                flightDetail.setFare(prices.get("totalAmount"));
            }
            flightDetailList.add(flightDetail);
        }
        return flightDetailList;
    }

    private List<FlightDetail> processData2(FlightDetailsRequest request, Map<String, Object> response) {
        List<FlightDetail> flightDetailList = new ArrayList<>();

        List<Map<String, Object>> legs = (List<Map<String, Object>>) response.get("legs");
        List<Map<String, Object>> trips = (List<Map<String, Object>>) response.get("trips");
        List<Map<String, Object>> fares = (List<Map<String, Object>>) response.get("fares");

        log.info("size of trips array : {}", trips.size());
        log.info("size of legs array :  {}", legs.size());
        log.info("size of fares array :  {}", fares.size());

        for(int i=0; i<trips.size(); i++) {
            FlightDetail flightDetail = new FlightDetail();
            String tripId = (String) trips.get(i).get("id");

            for(int j=0; j<fares.size(); j++) {
                String fareId = (String) fares.get(j).get("tripId");

                if(tripId.equals(fareId)){
                    flightDetail.setSource(request.getSource());
                    flightDetail.setDestination(request.getDestination());
                    Map<String, Integer> prices = (Map<String, Integer>) fares.get(j).get("price");
                    flightDetail.setFare(prices.get("totalAmount"));
                }
            }

            List<String> legIds = (List<String>) trips.get(i).get("legIds");
            for(int j=0; j<legs.size(); j++) {
                String legId = (String) legs.get(j).get("id");

                if(legIds.get(0).equals(legId)){
                    flightDetail.setSource((String) legs.get(j).get("departureAirportCode"));
                    flightDetail.setDestination((String) legs.get(j).get("arrivalAirportCode"));
                    flightDetail.setFlightDurationInMinutes(((Integer) legs.get(j).get("durationMinutes")).toString());
                    flightDetail.setDepartureTime(LocalTime.parse((String) legs.get(j).get("departureTime"), DateTimeFormatter.ISO_LOCAL_TIME));
                    flightDetail.setArrivalTime(LocalTime.parse((String) legs.get(j).get("arrivalTime"), DateTimeFormatter.ISO_LOCAL_TIME));
                }
            }

            flightDetailList.add(flightDetail);
        }

        return flightDetailList;
    }

}
