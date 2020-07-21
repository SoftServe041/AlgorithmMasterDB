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

    @OneToMany(mappedBy = "route")
    List<OrderEntity> order;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "route_hub",
            joinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "hub_id", referencedColumnName = "id"))
    private List<HubEntity> hubs;

    @Override
    public String toString() {
        StringBuilder route = new StringBuilder();
        for (HubEntity hub : hubs) {
            route.append(hub.name).append(" ");
        }
        return "route = [ " + route.toString() + "]";
    }
}
