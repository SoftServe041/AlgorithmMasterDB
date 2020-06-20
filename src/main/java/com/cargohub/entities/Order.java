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
    @GeneratedValue
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
    Hub departureHub;

    @OneToOne
    Hub arrivalHub;

    @OneToOne
    Cargo cargo;

    @Column
    @Enumerated
    DeliveryStatus deliveryStatus;
}
