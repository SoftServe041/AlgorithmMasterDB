package com.cargohub.entities;

import com.cargohub.entities.transports.Transporter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hub")
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    String name;

    @OneToMany
    List<Relation> relations;

    @OneToMany
    List<Transporter> transporters;

    @OneToMany
    List<Cargo> warehouse;
}
