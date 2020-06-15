package com.cargohub.entities;

import com.cargohub.entities.transports.Transporter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Hub {

    @Id
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
