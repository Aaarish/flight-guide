package com.aarish.flightguide.dto.responses;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDetail implements Comparable<FlightDetail> {
    private String source;
    private String destination;
    private String airline;
    private Integer fare;
    private String flightDurationInMinutes;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    @Override
    public int compareTo(FlightDetail other) {
        return this.getFare() - other.getFare();
    }
}
