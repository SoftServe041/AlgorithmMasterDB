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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "departure_hub_id")
    HubEntity departureHub;

    @ManyToOne
    @JoinColumn(name = "arrival_hub_id")
    HubEntity arrivalHub;

    @OneToOne
    @JoinColumn(name = "route_id")
    RouteEntity route;

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
