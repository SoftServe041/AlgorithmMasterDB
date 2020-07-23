package com.cargohub.entities;

import com.cargohub.entities.enums.TransporterType;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringExclude;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
@Table(name = "relation")
public class RelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    Double distance;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "connected_hub_id")
    HubEntity connectedHub;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_hub_id")
    HubEntity ownerHub;

    @Column
    @Enumerated(EnumType.STRING)
    TransporterType relationType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationEntity that = (RelationEntity) o;
        return Objects.equals(distance, that.distance) &&
                Objects.equals(connectedHub.getName(), that.connectedHub.getName()) &&
                Objects.equals(ownerHub.getName(), that.ownerHub.getName()) &&
                relationType == that.relationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, connectedHub.getName(), ownerHub.getName(), relationType);
    }

    @Override
    public String toString() {
        return "RelationEntity{" +
                "id=" + id +
                ", distance=" + distance +
                ", connectedHub=" + connectedHub.getName() +
                ", ownerHub=" + ownerHub.getName() +
                ", relationType=" + relationType +
                '}';
    }
}
