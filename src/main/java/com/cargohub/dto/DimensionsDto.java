package com.cargohub.dto;


import com.cargohub.entities.Dimensions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DimensionsDto {
    private Integer id;
    private Integer width;
    private Integer height;
    private Integer length;

    public Dimensions toDimensions() {
        Dimensions dimensions = new Dimensions();
        dimensions.setId(this.id);
        dimensions.setHeight(this.height);
        dimensions.setWidth(this.width);
        dimensions.setLength(this.length);
        return dimensions;
    }

    public static DimensionsDto toDto(Dimensions dimensions) {
        DimensionsDto dto = new DimensionsDto();
        dto.setId(dimensions.getId());
        dto.setHeight(dimensions.getHeight());
        dto.setLength(dimensions.getLength());
        dto.setWidth(dimensions.getWidth());
        return dto;
    }

}
