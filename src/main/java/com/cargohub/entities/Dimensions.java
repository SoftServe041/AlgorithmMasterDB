package com.cargohub.entities;

import com.cargohub.entities.transports.CarrierCompartment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "dimensions")
public class Dimensions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    Double width;

    @Column
    Double height;

    @Column
    Double length;

    @OneToOne(mappedBy = "volume", cascade = CascadeType.ALL)
    CarrierCompartment carrierCompartment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimensions that = (Dimensions) o;
        return width.equals(that.width) &&
                height.equals(that.height) &&
                length.equals(that.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, length);
    }
}
