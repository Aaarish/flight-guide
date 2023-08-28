package com.aarish.flightguide.dto.responses;

import lombok.*;

import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseWrapper {
    private Map<String, Object> remoteResponse;

}
