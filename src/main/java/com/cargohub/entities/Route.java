package com.cargohub.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(exclude = "id")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToMany
    @JoinTable(name = "route_hub",
            joinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "hub_id", referencedColumnName = "id"))
    private List<Hub> route;


}
