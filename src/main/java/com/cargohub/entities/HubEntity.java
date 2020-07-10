package com.cargohub.entities;

import com.cargohub.entities.transports.TransporterEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.criterion.Order;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hub")
public class HubEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    String name;

    @ManyToMany(mappedBy = "route", fetch = FetchType.LAZY)
    List<RouteEntity> routes;

    @OneToMany(orphanRemoval = true, mappedBy = "ownerHub")
    List<RelationEntity> relations;

    @OneToMany(orphanRemoval = true, mappedBy = "currentHub")
    List<TransporterEntity> transporters;
}
