package com.cargohub.entities;

import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.entities.transports.CarrierCompartment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cargo")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    Double weight;

    @Column
    String startingDestination;

    @Column
    String finalDestination;

    @OneToOne
    @JoinColumn(name = "dimensions_id")
    Dimensions dimensions;

    @OneToOne
    @JoinColumn(name = "cargo_position_id")
    CargoPosition cargoPosition;

    @ManyToOne
    @JoinColumn(name = "carrier_compartment_id")
    CarrierCompartment carrierCompartment;

    @OneToOne(mappedBy = "cargo")
    Order order;

    @Column
    @Enumerated(EnumType.STRING)
    DeliveryStatus deliveryStatus;
}
