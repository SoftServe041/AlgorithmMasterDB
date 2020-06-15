package com.cargohub.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    Integer id;

    //ToDo Generating rules discussing
    @Column
    String trackingId;

    @Column
    Double price;

    @ManyToOne
    Order order;

    @OneToOne
    Hub departureHub;

    @OneToOne
    Hub arrivalHub;

    @OneToOne
    Cargo cargo;

}
