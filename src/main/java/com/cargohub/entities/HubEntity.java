package com.cargohub.entities;

import com.cargohub.entities.transports.TransporterEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HubEntity hubEntity = (HubEntity) o;
        return Objects.equals(name, hubEntity.name) &&
                Objects.equals(relations, hubEntity.relations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, relations);
    }
}
