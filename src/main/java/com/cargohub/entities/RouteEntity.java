package com.cargohub.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
@Table(name = "route")
public class RouteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne(mappedBy = "route")
    OrderEntity order;

    @ManyToMany
    @JoinTable(name = "route_hub",
            joinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "hub_id", referencedColumnName = "id"))
    private List<HubEntity> route;


}
