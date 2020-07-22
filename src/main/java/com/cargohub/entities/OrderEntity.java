package com.cargohub.entities;


import com.cargohub.entities.enums.DeliveryStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
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

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "departure_hub_id")
    HubEntity departureHub;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "arrival_hub_id")
    HubEntity arrivalHub;

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    RouteEntity route;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<CargoEntity> cargoEntities;

    @Column
    @Enumerated(EnumType.STRING)
    DeliveryStatus deliveryStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created")
    private Date created;
}
