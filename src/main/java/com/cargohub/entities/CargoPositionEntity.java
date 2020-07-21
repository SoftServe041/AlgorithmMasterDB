package com.cargohub.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Table(name = "cargo_position")
public class CargoPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne(mappedBy =  "cargoPosition", cascade = CascadeType.ALL)
    CargoEntity cargoEntity;

    @Column
    Integer widthPos;

    @Column
    Integer heightPos;

    @Column
    Integer lengthPos;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CargoPositionEntity that = (CargoPositionEntity) o;
        return Objects.equals(widthPos, that.widthPos) &&
                Objects.equals(heightPos, that.heightPos) &&
                Objects.equals(lengthPos, that.lengthPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(widthPos, heightPos, lengthPos);
    }
}
