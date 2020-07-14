package com.cargohub.entities;

import com.cargohub.entities.transports.TransporterEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
@Table(name = "hub")
public class HubEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    String name;

    @ManyToMany(mappedBy = "hubs", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<RouteEntity> routes;

    @OneToMany(orphanRemoval = true, mappedBy = "ownerHub", cascade = CascadeType.ALL)
    List<RelationEntity> relations;

    @OneToMany(orphanRemoval = true, mappedBy = "currentHub", cascade = CascadeType.ALL)
    List<TransporterEntity> transporters;
}
