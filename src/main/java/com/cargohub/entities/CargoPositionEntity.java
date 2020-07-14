package com.cargohub.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
@Table(name = "cargo_position")
public class CargoPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne(mappedBy =  "cargoPosition", cascade = CascadeType.ALL)
    CargoEntity cargoEntity;

    @Column
    Integer widthPos;

    @Column
    Integer heightPos;

    @Column
    Integer lengthPos;
}
