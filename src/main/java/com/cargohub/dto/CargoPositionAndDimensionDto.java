package com.cargohub.dto;

import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.CargoPositionEntity;
import com.cargohub.entities.DimensionsEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CargoPositionAndDimensionDto {

    int widthPos;
    int heightPos;
    int lengthPos;

    double width;
    double height;
    double length;

    public static CargoPositionAndDimensionDto cargoToCarPos(CargoEntity cargoEntity) {
        CargoPositionAndDimensionDto cargoPositionAndDimensionDto = new CargoPositionAndDimensionDto();
        CargoPositionEntity cargoPosition = cargoEntity.getCargoPosition();
        cargoPositionAndDimensionDto.setWidthPos(cargoPosition.getWidthPos());
        cargoPositionAndDimensionDto.setHeightPos(cargoPosition.getHeightPos());
        cargoPositionAndDimensionDto.setLengthPos(cargoPosition.getLengthPos());
        DimensionsEntity dimension = cargoEntity.getDimensions();
        cargoPositionAndDimensionDto.setWidth(dimension.getWidth());
        cargoPositionAndDimensionDto.setHeight(dimension.getHeight());
        cargoPositionAndDimensionDto.setLength(dimension.getLength());
        return cargoPositionAndDimensionDto;
    }
}
