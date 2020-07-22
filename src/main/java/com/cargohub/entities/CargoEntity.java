package com.cargohub.entities;

import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
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

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "dimensions_id")
    DimensionsEntity dimensions;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "cargo_position_id")
    CargoPositionEntity cargoPosition;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "carrier_compartment_id")
    CarrierCompartmentEntity carrierCompartment;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    OrderEntity orderEntity;

    @Column
    @Enumerated(EnumType.STRING)
    DeliveryStatus deliveryStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CargoEntity that = (CargoEntity) o;
        return Objects.equals(weight, that.weight) &&
                Objects.equals(startingDestination, that.startingDestination) &&
                Objects.equals(finalDestination, that.finalDestination) &&
                Objects.equals(dimensions, that.dimensions) &&
                Objects.equals(cargoPosition, that.cargoPosition) &&
                deliveryStatus == that.deliveryStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, startingDestination, finalDestination, dimensions, cargoPosition, deliveryStatus);
    }
}
