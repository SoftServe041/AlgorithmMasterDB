package com.cargohub.dto;


import com.cargohub.entities.Dimensions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DimensionsRequestDto {
    private Integer width;
    private Integer height;
    private Integer length;

    public Dimensions toDimensions() {
        Dimensions dimensions = new Dimensions();
        dimensions.setHeight(this.height);
        dimensions.setWidth(this.width);
        dimensions.setLength(this.length);
        return dimensions;
    }

}
