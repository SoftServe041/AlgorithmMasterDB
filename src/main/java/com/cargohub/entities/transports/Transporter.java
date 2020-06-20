package com.cargohub.entities.transports;

import com.cargohub.entities.Cargo;
import com.cargohub.entities.Hub;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transporter {

    @Id
    @GeneratedValue
    Integer id;

    @OneToOne
    Hub currentHub;
    //    All compartments are in rectangular coordinates.
    //    Could be several carriages
    @OneToMany
    List<CarrierCompartment> compartments;

    @OneToMany
    List<Cargo> cargos;

    @OneToMany
    List<Hub> route;

    @Column
    @Enumerated(EnumType.STRING)
    TransportType type;

    @Column
    TransporterStatus status;
}
