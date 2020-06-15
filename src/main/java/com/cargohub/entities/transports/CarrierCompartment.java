package com.cargohub.entities.transports;


import com.cargohub.entities.Dimensions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CarrierCompartment {

    @Id
    Integer id;

    @Column
    Double maximumWeight;

    //Percents or cubic meters
    @Column
    Double freeSpace;

    @OneToOne
    Dimensions volume;
}
