package com.aarish.flightguide.services.impl;

import com.aarish.flightguide.dto.requests.ChatObject;
import com.aarish.flightguide.dto.requests.ItineraryRequest;
import com.aarish.flightguide.dto.requests.RequestForGpt;
import com.aarish.flightguide.enums.Role;
import com.aarish.flightguide.services.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {
    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.model}")
    private String apiModel;
    @Value("${openai.api.endpoint}")
    private String apiEndpoint;


    @Override
    public String createTravelItinerary(ItineraryRequest itineraryRequest) {
        String prompt = "You will be provided with two comma separated values, first value would be time period of vacation and second would be venue. Create a comprehensive travel itinerary based on the values. Include suggested activities, places to visit, accommodations (include both popular and affordable), and dining options.Use informal language and also conclude estimated price range for one person for the whole trip in US Dollars";

        ChatObject systemChatObject = ChatObject.builder()
                .role(Role.SYSTEM.getValue())
                .content(prompt)
                .build();

        String values = itineraryRequest.getStartDate()  + " - " + itineraryRequest.getEndDate() + ", " + itineraryRequest.getVenueCity();

        ChatObject userChatObject = ChatObject.builder()
                .role(Role.USER.getValue())
                .content(values)
                .build();


        ChatObject[] messages = new ChatObject[] {systemChatObject, userChatObject};

        RequestForGpt request = new RequestForGpt();
        request.setModel(apiModel);
        request.setMessages(messages);

        // Create headers with your API key
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Create an HTTP entity with headers and body
        HttpEntity<RequestForGpt> entity = new HttpEntity<>(request, headers);

        try {
            // Make a POST request to the API
            ResponseEntity<Map> responseEntity = restTemplate.exchange(apiEndpoint, HttpMethod.POST, entity, Map.class);

            // Check for a successful response
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Map body = responseEntity.getBody();

                List<Map> choices = (List<Map>) body.get("choices");
                Map map = choices.get(0);
                Map message = (Map) map.get("message");
                String response = (String) message.get("content");

                return response;
            } else {
                String errorResponse = "Error: " + responseEntity.getStatusCodeValue() + " - " + responseEntity.getBody();
                return errorResponse;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
