package com.cargohub.entities;

import com.cargohub.entities.transports.Truck;
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
    Truck transport;

    @Column
    @Enumerated
    DeliveryStatus deliveryStatus;

    @Column
    @Enumerated
    PaymentStatus paymentStatus;

}
