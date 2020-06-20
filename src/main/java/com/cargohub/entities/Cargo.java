package com.cargohub.entities;

import com.cargohub.dto.jar.DeliveryStatus;
import com.cargohub.entities.transports.Transporter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cargo {

    @Id
    @GeneratedValue
    Integer id;

    @Column
    Double weight;

    @OneToOne
    Dimensions dimensions;

    @OneToOne
    CargoPosition cargoPosition;

    @Column
    String startingDestination;

    @Column
    String finalDestination;

    @OneToOne
    Transporter transport;

    @Column
    @Enumerated
    DeliveryStatus deliveryStatus;

}
