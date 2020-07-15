package com.cargohub.entities.transports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transport_details")
public class TransportDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    Double averageSpeed;

    @Column
    Double pricePerKm;

    @Column
    Double cellSize;

    @Column
    @Enumerated(EnumType.STRING)
    TransporterType type;
}
