package com.cargohub.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Dimensions {

    @Id
    @GeneratedValue
    Integer id;

    @Column
    Integer width;

    @Column
    Integer height;

    @Column
    Integer length;
}
