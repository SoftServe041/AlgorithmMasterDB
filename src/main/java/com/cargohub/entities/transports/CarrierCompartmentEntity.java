package com.cargohub.entities.transports;


import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.DimensionsEntity;
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
public class CarrierCompartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    Double maximumWeight;

    @Column
    Double freeSpace;

    @OneToMany(mappedBy = "carrierCompartment", cascade = CascadeType.ALL)
    List<CargoEntity> cargoEntities;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "volume_id", referencedColumnName = "id")
    DimensionsEntity volume;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "transporter_id", referencedColumnName = "id")
    TransporterEntity transporter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrierCompartmentEntity that = (CarrierCompartmentEntity) o;
        return Objects.equals(maximumWeight, that.maximumWeight) &&
                Objects.equals(volume, that.volume);
    }
}
