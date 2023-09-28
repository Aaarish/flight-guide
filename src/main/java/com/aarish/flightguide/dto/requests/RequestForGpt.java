package com.aarish.flightguide.dto.requests;

import lombok.Data;

@Data
public class RequestForGpt {
    private String model;
    private ChatObject[] messages;
}
