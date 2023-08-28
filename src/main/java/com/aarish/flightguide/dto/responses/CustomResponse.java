package com.aarish.flightguide.dto.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomResponse {
    private FlightDetail[] flightDetails;

}
