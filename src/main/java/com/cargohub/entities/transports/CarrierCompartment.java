package com.cargohub.entities.transports;


import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.Dimensions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    @OneToMany(mappedBy = "carrierCompartment")
    List<CargoEntity> cargoEntities;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "volume_id", referencedColumnName = "id")
    Dimensions volume;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "transporter_id", referencedColumnName = "id")
    Transporter transporter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrierCompartment that = (CarrierCompartment) o;
        return Objects.equals(maximumWeight, that.maximumWeight) &&
                Objects.equals(volume, that.volume);
    }
}
