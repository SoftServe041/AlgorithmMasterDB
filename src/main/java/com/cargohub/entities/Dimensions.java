package com.cargohub.entities;

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
    Integer width;

    @Column
    Integer height;

    @Column
    Integer length;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimensions that = (Dimensions) o;
        return  width.equals(that.width) &&
                height.equals(that.height) &&
                length.equals(that.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, length);
    }
}
