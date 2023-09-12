package com.aarish.flightguide.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "airports")
public class Airport {
    @Id
    private String airportCode;
    private String airportName;
    private String cityCode;

}
