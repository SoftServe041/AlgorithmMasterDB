package com.cargohub.entities.transports;

import com.cargohub.entities.enums.TransporterType;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
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
