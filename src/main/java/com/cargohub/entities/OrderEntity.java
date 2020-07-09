package com.cargohub.entities;


import com.cargohub.entities.enums.DeliveryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cargo_order")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    String trackingId;

    @Column
    Integer userId;

    @Column
    Double price;

    @Column
    Date estimatedDeliveryDate;

    @OneToOne
    @JoinColumn(name = "departure_hub_id")
    Hub departureHub;

    @OneToOne
    @JoinColumn(name = "arrival_hub_id")
    Hub arrivalHub;

    @OneToOne
    @JoinColumn(name = "route_id")
    Route route;

    @OneToMany(mappedBy = "orderEntity")
    List<CargoEntity> cargoEntities;

    @Column
    @Enumerated(EnumType.STRING)
    DeliveryStatus deliveryStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created")
    private Date created;
}
