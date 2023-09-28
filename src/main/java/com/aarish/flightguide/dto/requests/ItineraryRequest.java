package com.aarish.flightguide.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryRequest {
    private String startDate;
    private String endDate;
    private String venueCity;

}
