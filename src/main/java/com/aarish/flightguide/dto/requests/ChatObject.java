package com.aarish.flightguide.dto.requests;

import com.aarish.flightguide.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatObject {
    private String role;
    private String content;

}
