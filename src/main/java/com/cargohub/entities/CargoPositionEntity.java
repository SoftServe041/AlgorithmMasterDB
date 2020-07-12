package com.cargohub.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cargo_position")
public class CargoPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne(mappedBy =  "cargoPosition")
    CargoEntity cargoEntity;

    @Column
    Integer widthPos;

    @Column
    Integer heightPos;

    @Column
    Integer lengthPos;
}