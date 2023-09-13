package com.aarish.flightguide.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDetailsRequest {
    private String source;
    private String destination;
    @JsonProperty("date")
    private String dateString;
    @JsonProperty("cabin_class")
    private String cabinClass;

    @JsonProperty("adults")
    private Integer numOfAdults;
    @JsonProperty("children")
    private Integer numOfChildren;
    @JsonProperty("infants")
    private Integer numOfInfants;

}
