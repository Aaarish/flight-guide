package com.aarish.flightguide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Role {

    SYSTEM("system"),
    USER("user")
    ;

    private String value;

}
