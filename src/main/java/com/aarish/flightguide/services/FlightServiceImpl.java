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
    private static final String REMOTE_API_BASE_URL = "https://api.flightapi.io/onewaytrip/64ece16f3959e563b552f317";

    private final RestTemplate restTemplate;

    @Override
    public CustomResponse getFlightDetails(FlightDetailsRequest flightDetailsRequest) {
        String url = REMOTE_API_BASE_URL + "/" + flightDetailsRequest.getSource() + "/" + flightDetailsRequest.getDestination() + "/2023-09-03/" + 1 + "/" + 0 + "/" + 0 + "/" + flightDetailsRequest.getCabinClass() + "/USD";
        Map<String, Object> response = null;

        try {
            log.info("connecting to the remote api !!!");
            response = restTemplate.getForObject(url, HashMap.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to connect to remote api !");
        }

        List<FlightDetail> flightDetailList = processData(flightDetailsRequest, response);
        log.info("flights list size: {}", flightDetailList.size());

        List<FlightDetail> finalResponseList = flightDetailList.stream()
                .filter(flightDetail -> flightDetail.getFare() != null)
                .toList();

        return CustomResponse.builder()
                .flightDetails(finalResponseList.toArray(new FlightDetail[finalResponseList.size()]))
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

}
