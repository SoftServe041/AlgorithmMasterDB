package com.cargohub.entities;

import com.cargohub.entities.transports.TransportType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TransportDetails {

    @Id
    Integer id;

    @Column
    @Enumerated
    TransportType type;

    @Column
    Double averageSpeed;

    @Column
    Double prisePerKm;
}
