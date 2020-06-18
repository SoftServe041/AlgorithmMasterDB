package com.cargohub.entities.transports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TransportDetails {

    @Id
    @GeneratedValue
    Integer id;

    @Column
    @Enumerated
    TransportType type;

    @Column
    Double averageSpeed;

    @Column
    Double prisePerKm;
}
