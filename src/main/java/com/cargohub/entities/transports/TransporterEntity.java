package com.cargohub.entities.transports;

import com.cargohub.entities.HubEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transporter")
public class TransporterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "current_hub_id", referencedColumnName = "id")
    HubEntity currentHub;

    @OneToMany(mappedBy = "transporter", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<CarrierCompartmentEntity> compartments;

    @OneToMany
    @JoinTable(name = "transporter_route",
            joinColumns = {@JoinColumn(name = "transporter_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "hub_id", referencedColumnName = "id")})
    List<HubEntity> route;

    @Column
    @Enumerated(EnumType.STRING)
    TransporterType type;

    @Column
    @Enumerated(EnumType.STRING)
    TransporterStatus status;
}
