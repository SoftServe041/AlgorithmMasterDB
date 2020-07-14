package com.cargohub.entities;

import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
@Table(name = "cargo")
public class CargoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    Double weight;

    @Column
    String startingDestination;

    @Column
    String finalDestination;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "dimensions_id")
    DimensionsEntity dimensions;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cargo_position_id")
    CargoPositionEntity cargoPosition;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "carrier_compartment_id")
    CarrierCompartmentEntity carrierCompartment;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    OrderEntity orderEntity;

    @Column
    @Enumerated(EnumType.STRING)
    DeliveryStatus deliveryStatus;
}
