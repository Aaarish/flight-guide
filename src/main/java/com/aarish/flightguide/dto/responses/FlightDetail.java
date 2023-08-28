package com.aarish.flightguide.dto.responses;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDetail {
    private String source;
    private String destination;
    private String airline;
    private Integer fare;
    private String flightDurationInHours;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

}
