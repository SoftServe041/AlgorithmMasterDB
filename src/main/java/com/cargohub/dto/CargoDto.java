package com.cargohub.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CargoDto {

    Double weight;
    DimensionsRequestDto dimensions;

}
