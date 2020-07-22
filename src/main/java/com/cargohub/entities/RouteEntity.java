package com.cargohub.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
@Table(name = "route")
@Transactional
public class RouteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToMany(mappedBy = "route")
    List<OrderEntity> order;

    @LazyCollection(LazyCollectionOption.FALSE)
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
