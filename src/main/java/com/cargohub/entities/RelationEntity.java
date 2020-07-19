package com.cargohub.entities;

import com.cargohub.entities.enums.TransporterType;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringExclude;

import javax.persistence.*;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "connected_hub_id")
    HubEntity connectedHub;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_hub_id")
    HubEntity ownerHub;

    @Column
    @Enumerated(EnumType.STRING)
    TransporterType relationType;
}
