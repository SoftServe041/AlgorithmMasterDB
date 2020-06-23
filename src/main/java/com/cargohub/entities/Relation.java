package com.cargohub.entities;

import com.cargohub.entities.transports.TransporterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "relation")
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    Double distance;

    @ManyToOne
    @JoinColumn(name = "connected_hub_id")
    Hub connectedHub;

    @ManyToOne
    @JoinColumn(name = "owner_hub_id")
    Hub ownerHub;

    @Column
    @Enumerated(EnumType.STRING)
    TransporterType relationType;
}
