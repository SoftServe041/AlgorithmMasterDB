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
public class Hub {

    @Id
    @GeneratedValue
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
