package com.cargohub.dto;

import com.cargohub.entities.CargoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CargoDto {

    private Double weight;
    private Double width;
    private Double height;
    private Double length;

    static CargoDto toDto(CargoEntity cargoEntity) {
        CargoDto cargoDto = new CargoDto();
        cargoDto.setWeight(cargoEntity.getWeight());
        cargoDto.setHeight(cargoEntity.getDimensions().getHeight());
        cargoDto.setWidth(cargoEntity.getDimensions().getWidth());
        cargoDto.setLength(cargoEntity.getDimensions().getLength());
        return cargoDto;
    }
}
