package com.cargohub.entities;

import com.cargohub.entities.transports.TransportType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Relation {

    @Id
    @GeneratedValue
    Integer id;

    @Column
    Double distance;

    @Column
    @Enumerated
    TransportType relationType;

    @OneToOne
    Hub connectedHub;
}
