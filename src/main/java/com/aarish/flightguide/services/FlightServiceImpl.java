package com.aarish.flightguide.services;

import com.aarish.flightguide.dto.requests.FlightDetailsRequest;
import com.aarish.flightguide.dto.responses.CustomResponse;
import com.aarish.flightguide.dto.responses.FlightDetail;
import com.aarish.flightguide.entities.Airport;
import com.aarish.flightguide.entities.City;
import com.aarish.flightguide.repositories.AirportRepo;
import com.aarish.flightguide.repositories.CityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {
    private static final String REMOTE_API_BASE_URL = "https://api.flightapi.io/onewaytrip/6500ac41d3c92be52698e9a6";

    private final RestTemplate restTemplate;
    private final AirportRepo airportRepo;
    private final CityRepo cityRepo;

    @Override
    public CustomResponse getAllFlightDetails(FlightDetailsRequest flightDetailsRequest) {
        City sourceCity = cityRepo.findByCityName(flightDetailsRequest.getSource().toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("No city with this name"));

        City destinationCity = cityRepo.findByCityName(flightDetailsRequest.getDestination().toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("No city with this name"));

        Airport sourceAirport = airportRepo.findByCityCode(sourceCity.getCode())
                .orElseThrow(() -> new NoSuchElementException("No airport with this city code"));

        Airport destinationAirport = airportRepo.findByCityCode(destinationCity.getCode())
                .orElseThrow(() -> new NoSuchElementException("No airport with this city code"));

        log.info("source airport code : {}", sourceAirport.getAirportCode());
        log.info("destination airport code : {}", destinationAirport.getAirportCode());
        log.info("source airport name : {}", sourceAirport.getAirportName());
        log.info("destination airport name : {}", destinationAirport.getAirportName());

        String url = REMOTE_API_BASE_URL + "/" + sourceAirport.getAirportCode() + "/" + destinationAirport.getAirportCode() + "/2023-12-20/" + 1 + "/" + 0 + "/" + 0 + "/" + flightDetailsRequest.getCabinClass() + "/USD";
        Map<String, Object> response = null;

        try {
            log.info("connecting to the remote api !!!");
            response = restTemplate.getForObject(url, HashMap.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to connect to remote api : " + e);
        }

        List<FlightDetail> flightDetailList = processData(flightDetailsRequest, response);
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


    // private methods
    private List<FlightDetail> processData(FlightDetailsRequest request, Map<String, Object> response) {
        List<FlightDetail> flightDetailList = new ArrayList<>();

        List<Map<String, Object>> legs = (List<Map<String, Object>>) response.get("legs");
        List<Map<String, Object>> trips = (List<Map<String, Object>>) response.get("trips");
        List<Map<String, Object>> fares = (List<Map<String, Object>>) response.get("fares");
        Map<String, Integer> faresCount = (Map<String, Integer>) response.get("faresCount");
        List<Map<String, String>> airports = (List<Map<String, String>>) response.get("airports");
        List<Map<String, String>> cities = (List<Map<String, String>>) response.get("cities");

        log.info("size of trips array : {}", trips.size());
        log.info("size of legs array :  {}", legs.size());
        log.info("size of fares array :  {}", fares.size());
        log.info("size of faresCount array :  {}", faresCount.size());
        log.info("size of airports array :  {}", airports.size());
        log.info("size of cities array :  {}", cities.size());

//        populateCitiesAndAirports(airports, cities);

        for(int i=0; i<trips.size(); i++) {
            FlightDetail flightDetail = new FlightDetail();

            flightDetail.setSource(request.getSource());
            flightDetail.setDestination(request.getDestination());

            String tripId = (String) trips.get(i).get("id");

            int minFareForTrip = Integer.MAX_VALUE;

            for(int j=0; j<fares.size(); j++) {
                String fareId = (String) fares.get(j).get("tripId");
                int remainingSeatsCount = (Integer) fares.get(j).get("remainingSeatsCount");

                if(tripId.equals(fareId) && remainingSeatsCount > 0){
                    Map<String, Integer> prices = (Map<String, Integer>) fares.get(j).get("price");
                    minFareForTrip = Math.min(minFareForTrip, prices.get("totalAmount"));
                }
            }

            flightDetail.setFare(minFareForTrip);

            List<String> tripLegIds = (List<String>) trips.get(i).get("legIds");
            int numOfLegs = tripLegIds.size();

            Integer totalDurationInMinutesForCurrentTrip = 0;
            LocalTime departureTime = null;
            LocalTime arrivalTime = null;

            for(int leg=0; leg<numOfLegs; leg++) {
                String legIdForCurrentTrip = tripLegIds.get(leg);

                for(int j=0; j<legs.size(); j++) {
                    String legId = (String) legs.get(j).get("id");

                    if(legIdForCurrentTrip.equals(legId)) {
                        Integer durationInMinutesForCurrentLeg = (Integer) legs.get(j).get("durationMinutes");
                        totalDurationInMinutesForCurrentTrip += durationInMinutesForCurrentLeg;

                        if(leg == 0) departureTime = LocalTime.parse((String) legs.get(j).get("departureTime"), DateTimeFormatter.ISO_LOCAL_TIME);
                        if(leg == numOfLegs - 1) arrivalTime = LocalTime.parse((String) legs.get(j).get("arrivalTime"), DateTimeFormatter.ISO_LOCAL_TIME);
                    }
                }
            }

            flightDetail.setFlightDurationInMinutes(totalDurationInMinutesForCurrentTrip.toString());
            flightDetail.setDepartureTime(departureTime);
            flightDetail.setArrivalTime(arrivalTime);

            flightDetailList.add(flightDetail);
        }

        return flightDetailList.stream()
                .filter(flightDetail -> flightDetail.getFare() != null && flightDetail.getFare() != Integer.MAX_VALUE)
                .collect(Collectors.toList());
    }

    private void populateCitiesAndAirports(List<Map<String, String>> airports, List<Map<String, String>> cities) {
        int numOfCities = cities.size();
        int numOfAirports = airports.size();

        for (int i=0; i<numOfCities; i++) {
            City city = new City();

            String code = cities.get(i).get("code");
            String cityName = cities.get(i).get("name");
            String countryCode = cities.get(i).get("countryCode");

            city.setCode(code);
            city.setCityName(cityName.toLowerCase());
            city.setCountryCode(countryCode);

            cityRepo.save(city);
        }

        for (int i=0; i<numOfAirports; i++) {
            Airport airport = new Airport();

            String code = airports.get(i).get("code");
            String airportName = airports.get(i).get("name");
            String cityCode = airports.get(i).get("cityCode");

            airport.setAirportCode(code);
            airport.setAirportName(airportName.toLowerCase());
            airport.setCityCode(cityCode);

            airportRepo.save(airport);
        }
    }

}
