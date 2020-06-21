package com.cargohub.entities;


import com.cargohub.dto.jar.DeliveryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cargo_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    //ToDo Generating rules discussing
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
    @JoinColumn(name = "cargo_id")
    Cargo cargo;

    @Column
    @Enumerated(EnumType.STRING)
    DeliveryStatus deliveryStatus;
}
