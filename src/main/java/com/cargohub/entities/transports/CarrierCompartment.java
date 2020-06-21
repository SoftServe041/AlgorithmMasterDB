package com.cargohub.entities.transports;


import com.cargohub.entities.Cargo;
import com.cargohub.entities.Dimensions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "carrier_compartment")
public class CarrierCompartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    Double maximumWeight;

    //Percents or cubic meters
    @Column
    Double freeSpace;

    @OneToMany
    List<Cargo> cargos;

    @OneToOne
    @JoinColumn(name = "volume_id", referencedColumnName = "id")
    Dimensions volume;

    @ManyToOne
    @JoinColumn(name = "transporter_id", referencedColumnName = "id")
    Transporter transporter;
}
