package com.aarish.flightguide.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDetailsRequest {
    private String source;
    private String destination;
    private String cabinClass;

}
